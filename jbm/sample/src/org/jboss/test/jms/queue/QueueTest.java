package org.jboss.test.jms.queue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;

import org.jboss.test.jms.common.TestBase;


/**
 * This example creates a JMS Connection to a JBoss Messaging instance and uses it to create a
 * session and a message producer, which sends a message to the queue "queue/testQueue".  Then, the
 * example uses the same connection to create a consumer that that reads a single message from the
 * queue. The example is considered successful if the message consumer receives without any error
 * the message that was sent by the producer.
 * 
 * Since this example is also used by the smoke test, it is essential that the VM exits with exit
 * code 0 in case of successful execution and a non-zero value on failure.
 *
 */
public class QueueTest extends TestBase {
   
	public void test() throws Exception {

		String destinationName = getDestinationJNDIName();

		InitialContext ic = null;
		ConnectionFactory cf = null;
		Connection connection =  null;

		try {
			ic = new InitialContext();

			cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
			Queue queue = (Queue) ic.lookup(destinationName);
			log("Queue " + destinationName + " exists");
         
			connection = cf.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer sender = session.createProducer(queue);
         
			TextMessage message = session.createTextMessage("Hello!");
			sender.send(message);
			log("The message was successfully sent to the " + queue.getQueueName() + " queue");
         
			MessageConsumer consumer = session.createConsumer(queue);

			connection.start();

			message = (TextMessage)consumer.receive(2000);

			log("Received message: " + message.getText());
			assertEquals("Hello!", message.getText());

			displayProviderInfo(connection.getMetaData());
                 
		} finally {   
			
			if (ic != null) {
				try {
					ic.close();
				} catch (Exception e) {
					throw e;
				}
			}
         
         // ALWAYS close your connection in a finally block to avoid leaks.
         // Closing connection also takes care of closing its related objects e.g. sessions.
			closeConnection(connection);
		}
	}
   
	private void closeConnection(Connection con) {
		
		try {
			if (con != null) {
				con.close();
			}
		} catch (JMSException jmse) {
			log("Could not close connection " + con + " exception was " + jmse);
		}
	}

	protected boolean isQueueTest() {
		return true;
	}
   
	public static void main(String[] args) {
		new QueueTest().run();
	}
   
}
