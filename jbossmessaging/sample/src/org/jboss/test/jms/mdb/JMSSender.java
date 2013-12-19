package org.jboss.test.jms.mdb;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.jboss.test.jms.common.TestBase;

/**
 * This example deploys a simple EJB3 Message Driven Bean that processes messages sent to a test
 * queue. Once it receives a message and "processes" it, the MDB sends an acknowledgment message to
 * a temporary destination created by the sender for this purpose. The example is considered
 * successful if the sender receives the acknowledgment message.
 *
 * Since this example is also used by the smoke test, it is essential that the VM exits with exit
 * code 0 in case of successful execution and a non-zero value on failure.
 * 
 */
public class JMSSender extends TestBase {
	
	public void test() throws Exception {
	   
		String destinationName = getDestinationJNDIName();

		InitialContext ic = new InitialContext();

		ConnectionFactory cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
		Queue queue = (Queue)ic.lookup(destinationName);

		log("Queue " + destinationName + " exists");

		Connection connection = cf.createConnection();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer sender = session.createProducer(queue);

		Queue temporaryQueue = session.createTemporaryQueue();
		MessageConsumer consumer = session.createConsumer(temporaryQueue);

		TextMessage message = session.createTextMessage("Hello!");
		message.setJMSReplyTo(temporaryQueue);

		sender.send(message);

		log("The " + message.getText() + " message was successfully sent to the " + queue.getQueueName() + " queue");

		connection.start();

		message = (TextMessage)consumer.receive(5000);

		if (message == null) {
			throw new Exception("Have not received any reply. The example failed!");
		}

		log("Received message: " + message.getText());

		displayProviderInfo(connection.getMetaData());

		connection.close();
	}

	protected boolean isQueueTest() {
		return true;
	}

	public static void main(String[] args) {
		new JMSSender().run();
	}

}
