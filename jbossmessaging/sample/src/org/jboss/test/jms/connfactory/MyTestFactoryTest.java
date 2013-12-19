package org.jboss.test.jms.connfactory;

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
 * The example creates a connection to the default provider and uses the
 * connection to send a message to the queue "queue/testQueue". Then, the
 * example creates a second connection to the provider and uses it to receive
 * the message.
 * 
 * Since this example is also used by the smoke test, it is essential that the
 * VM exits with exit code 0 in case of successful execution and a non-zero
 * value on failure.
 * 
 */
public class MyTestFactoryTest extends TestBase {

	public void test() throws Exception {

		String destinationName = getDestinationJNDIName();

		InitialContext ic = null;
		ConnectionFactory cf = null;
		Connection connection = null;
		Connection connection2 = null;

		try {
			ic = new InitialContext();

			cf = (ConnectionFactory) ic.lookup("/MyTestConnectionFactory");
			log("connection factory: " + cf);
			Queue queue = (Queue) ic.lookup(destinationName);
			log("Queue " + destinationName + " exists");

			connection = cf.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer sender = session.createProducer(queue);

			TextMessage message = session.createTextMessage("Hello!");
			sender.send(message);
			log("The message was successfully sent to the " + queue.getQueueName() + " queue");

			connection2 = cf.createConnection();
			Session session2 = connection2.createSession(false,	Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = session2.createConsumer(queue);

			connection2.start();

			message = (TextMessage) consumer.receive(2000);
			log("Received message: " + message.getText());
			assertEquals("Hello!", message.getText());

			displayProviderInfo(connection2.getMetaData());

		} finally {
			if (ic != null) {
				try {
					ic.close();
				} catch (Exception e) {
					throw e;
				}
			}

			// ALWAYS close your connection in a finally block to avoid leaks
			// Closing connection also takes care of closing its related objects
			// e.g. sessions
			closeConnection(connection);
			closeConnection(connection2);
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
		new MyTestFactoryTest().run();
	}

}
