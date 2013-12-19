package org.jboss.test.jms.topic;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;

import org.jboss.test.jms.common.TestBase;


/**
 * The example creates a connection to the default provider and uses the connection to send a
 * message to the topic "queue/testTopic". The message must be received by a topic subscriber.
 *
 * Since this example is also used by the smoke test, it is essential that the VM exits with exit
 * code 0 in case of successful execution and a non-zero value on failure.
 *
 */
public class TopicTest extends TestBase {
	
	public void test() throws Exception {
		
		String destinationName = getDestinationJNDIName();

		InitialContext ic = null;
		Connection connection = null;
      
		try {
			ic = new InitialContext();
         
			ConnectionFactory cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
			Topic topic = (Topic) ic.lookup(destinationName);
			log("Topic " + destinationName + " exists");
         
			connection = cf.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer publisher = session.createProducer(topic);
			MessageConsumer subscriber = session.createConsumer(topic);
         
			TestListener messageListener = new TestListener();
			subscriber.setMessageListener(messageListener);
			connection.start();
         
			TextMessage message = session.createTextMessage("Hello!");
			publisher.send(message);
			log("The message was successfully published on the topic");
         
			messageListener.waitForMessage();
         
			message = (TextMessage) messageListener.getMessage();
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
			// Closing connection also takes care of closing its related objects
			// e.g. sessions.
			closeConnection(connection);
		}
	}
   
	private void closeConnection(Connection con) throws JMSException {

		try {
			if (con != null) {
				con.close();
			}
		} catch (JMSException jmse) {
			log("Could not close connection " + con + " exception was " + jmse);
		}
	}
   
	protected boolean isQueueTest() {
		return false;
	}

	public static void main(String[] args) {
		new TopicTest().run();
	}

}
