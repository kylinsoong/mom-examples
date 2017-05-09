package com.kylin.jms.hornetq.cluster;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import com.kylin.jms.hornetq.HornetQBase;
import com.kylin.jms.resource.Resource;

public class ClientSideLoadBalancing extends HornetQBase {
	
	public static void main(final String[] args) {
		new ClientSideLoadBalancing().run(args);
	}

	public boolean run() throws Exception {

		InitialContext initialContext = null;

		Connection connectionA = null;
		Connection connectionB = null;
		Connection connectionC = null;
		
		try {
			// Step 1. Get an initial context for looking up JNDI from server 0
			initialContext = getContext(0);

			// Step 2. Look-up the JMS Queue object from JNDI
			Queue queue = (Queue)initialContext.lookup("/queue/testQueue");

			// Step 3. Look-up a JMS Connection Factory object from JNDI on server 0
			ConnectionFactory connectionFactory = (ConnectionFactory)initialContext.lookup("/ConnectionFactory");

			// Step 4. We create 3 JMS connections from the same connection factory. Since we are using round-robin
			// load-balancing this should result in each sessions being connected to a different node of the cluster
			Connection conn = connectionFactory.createConnection();
	         
			// Wait a little while to make sure broadcasts from all nodes have reached the client
			Thread.sleep(5000);
			connectionA = connectionFactory.createConnection();
			connectionB = connectionFactory.createConnection();
			connectionC = connectionFactory.createConnection();
			conn.close();

			// Step 5. We create JMS Sessions
			Session sessionA = connectionA.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Session sessionB = connectionB.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Session sessionC = connectionC.createSession(false, Session.AUTO_ACKNOWLEDGE);

			System.out.println("Session A - " + ((org.hornetq.core.client.impl.DelegatingSession) ((org.hornetq.jms.client.HornetQSession) sessionA).getCoreSession()).getConnection().getRemoteAddress() );
			System.out.println("Session B - " + ((org.hornetq.core.client.impl.DelegatingSession) ((org.hornetq.jms.client.HornetQSession) sessionB).getCoreSession()).getConnection().getRemoteAddress() );
			System.out.println("Session C - " + ((org.hornetq.core.client.impl.DelegatingSession) ((org.hornetq.jms.client.HornetQSession) sessionC).getCoreSession()).getConnection().getRemoteAddress() );

	         // Step 6. We create JMS MessageProducer objects on the sessions
			MessageProducer producerA = sessionA.createProducer(queue);
			MessageProducer producerB = sessionB.createProducer(queue);
			MessageProducer producerC = sessionC.createProducer(queue);

	         // Step 7. We send some messages on each producer
			final int numMessages = Integer.parseInt(Resource.get("DEFAULT_MESSAGE_COUNT"));

			for (int i = 0; i < numMessages; i++) {
				
				TextMessage messageA = sessionA.createTextMessage("A:This is text message " + i);
	            producerA.send(messageA);
	            System.out.println("Sent message: " + messageA.getText());

	            TextMessage messageB = sessionB.createTextMessage("B:This is text message " + i);
	            producerB.send(messageB);
	            System.out.println("Sent message: " + messageB.getText());

	            TextMessage messageC = sessionC.createTextMessage("C:This is text message " + i);
	            producerC.send(messageC);
	            System.out.println("Sent message: " + messageC.getText());
	         }

	         // Step 8. We start the connection to consume messages
			connectionA.start();
			connectionB.start();
			connectionC.start();

	         // Step 9. We consume messages from the 3 session, one at a time.
	         // We try to consume one more message than expected from each session. If
	         // the session were not properly load-balanced, we would be missing a
	         // message from one of the sessions at the end.
			consume(sessionA, queue, numMessages, "A");
			consume(sessionB, queue, numMessages, "B");
			consume(sessionC, queue, numMessages, "C");

			return true;
		} finally {
		
			// Step 10. Be sure to close our resources!
			if (connectionA != null) {
				connectionA.close();
			}
			if (connectionB != null) {
				connectionB.close();
			}
			if (connectionC != null) {
				connectionC.close();
			}

			if (initialContext != null) {
				initialContext.close();
			}
		}
	}

	private void consume(Session session, Queue queue, int numMessages, String node) throws JMSException {
		 
		MessageConsumer consumer = session.createConsumer(queue);

		for (int i = 0; i < numMessages; i++) {
			TextMessage message = (TextMessage)consumer.receive(2000);
			System.out.println("Got message: " + message.getText() + " from node " + node);
		}

		System.out.println("receive other message from node " + node + ": " + consumer.receive(2000));
	}
}
