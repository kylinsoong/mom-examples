package org.jboss.test.jms.bridge;

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
 * This example creates a JMS Connection to a JBoss Messaging instance and then sends a message to a source queue.
 * It then waits to receive the same messages from the target queue.
 * 
 * The example ant script will deploy a message bridge that moves messages from the source to the target queue.
 * 
 * 
 * Since this example is also used by the smoke test, it is essential that the VM exits with exit
 * code 0 in case of successful execution and a non-zero value on failure.
 *
 */
public class BridgeExample extends TestBase {
   
	public void test() throws Exception {

		String source = System.getProperty("example.source.queue");

		String target = System.getProperty("example.target.queue");

		InitialContext ic = null;
		ConnectionFactory cf = null;
		Connection connection =  null;

		try {
			ic = new InitialContext();
         
			cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
			Queue sourceQueue = (Queue) ic.lookup("/queue/" + source);
			log("Queue " + sourceQueue + " exists");
         
			Queue targetQueue = (Queue) ic.lookup("/queue/" + target);
			log("Queue " + targetQueue + " exists");
         
			connection = cf.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer sender = session.createProducer(sourceQueue);
         
			final int NUM_MESSAGES = 10;
         
			for (int i = 0; i < NUM_MESSAGES; i++) {
				TextMessage message = session.createTextMessage("Hello!" + i);	
         	
				sender.send(message);
         	
				log("The message was successfully sent to the " + sourceQueue.getQueueName() + " queue");
			}
         
			MessageConsumer consumer =  session.createConsumer(targetQueue);
         
			connection.start();
         
			for (int i = 0; i < NUM_MESSAGES; i++) {
				TextMessage message = (TextMessage)consumer.receive(10000);
         	
				assertEquals("Hello!" + i, message.getText());
         	
				log("The message was received successfully from the " + targetQueue.getQueueName() + " queue");
			}
                  
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
		new BridgeExample().run();
	}

}
