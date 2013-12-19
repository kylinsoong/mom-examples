package org.jboss.test.jms.mdb;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;

import javax.naming.InitialContext;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.Session;
import javax.jms.MessageListener;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.Connection;
import javax.jms.MessageProducer;

import org.apache.log4j.Logger;

/**
 * A MDB3 EJB example.
 * 
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/testQueue"),
		@ActivationConfigProperty(propertyName = "DLQMaxResent", propertyValue = "10") })
public class EJB3MDBTest implements MessageListener {
	
	private final Logger logger = Logger.getLogger(EJB3MDBTest.class);
	
	private MessageDrivenContext ctx;
	
	private ConnectionFactory cf = null;
	
	private static Set<String> messageIDs = new HashSet<String>();
	
	public void onMessage(Message m) {
		
//		businessLogic(m);
		
//		mdbFailedtest(m);
		
		mdbRedeliverTimesTest(m);
	}
	
	private static int count = 0;

	private void mdbRedeliverTimesTest(Message m) {
		
		printEnter();
		logger.info("Testing MDB redeliver Times " + count + " Start...");
		
		count ++ ;
		
		try {
			TextMessage tm = (TextMessage)m;
			String text = tm.getText();
			logger.info("message [" + text + "] received, messageId: " + tm.getJMSMessageID() + ", Message destination: " + tm.getJMSDestination());
			
			logger.info("message " + (tm.getJMSRedelivered() ? "" : "NOT ")+ "marked as \"redelivered\"");
			
			throw new RuntimeException("Unfortunate events happen ...");
			
		} catch (JMSException e) {
			if(ctx != null) {
				ctx.setRollbackOnly();
			}
			e.printStackTrace();
			logger.error("The Message Driven Bean failed!");
		} 
	}

	private void printEnter() {
		for(int i = 10 ; i > 0 ; i --){
			logger.info(" -- ");
		}
	}

	private void mdbFailedtest(Message m) {
		
		logger.info("Testing MDB redeliver mechanism");
		
		Session session = null;
		Connection conn = null;
		
		try {
			TextMessage tm = (TextMessage)m;

			String text = tm.getText();

			logger.info("message [" + text + "] received");

			logger.info("message " + (tm.getJMSRedelivered() ? "" : "NOT ")+ "marked as \"redelivered\"");
			
			synchronized(messageIDs) {
				if (!messageIDs.contains(tm.getJMSMessageID())) {
					messageIDs.add(m.getJMSMessageID());
					logger.info("The message has \"broken\" our MDB ...");
					throw new RuntimeException("Unfortunate events happen ...");
				} else {
					logger.info("The message is already \"known\", so let it be processed");
				}
			}
			
			String result = process(text);
			logger.info("message processed, result: " + result);
			
			conn = getConnection();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Destination replyTo = m.getJMSReplyTo();
			logger.info("Message bundled destination: " + replyTo.toString());
			
			MessageProducer producer = session.createProducer(replyTo);
			TextMessage reply = session.createTextMessage(result);
			
			producer.send(reply);
			producer.close();
			
		} catch (JMSException e) {
			if(ctx != null) {
				ctx.setRollbackOnly();
			}
			e.printStackTrace();
			logger.error("The Message Driven Bean failed!");
		} finally {
			if(null != conn) {
				try {
					closeConnection(conn);
				} catch (Exception e) {
					logger.error("Could not close the connection!" + e);
				}
			}
		}
	}

	private Connection getConnection() throws JMSException {
		 
		if (cf == null) {
			try {
				InitialContext ic = new InitialContext();

				cf = (ConnectionFactory)ic.lookup("java:/JmsXA");

				ic.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new JMSException("Failure to get connection factory: " + e.getMessage());
			}
		}
		
		Connection connection = null;

		try {
			connection = cf.createConnection();
			connection.start();

		} catch (JMSException e) {
			if (connection != null) {
				closeConnection(connection);
			}
			logger.error("Failed to get connection... exception is " + e);
			throw e;
		}

		return connection;
	}

	private String process(String text) {

		String result = "";

		for (int i = 0; i < text.length(); i++) {
			result = text.charAt(i) + result;
		}
		return "Redeliver Test Process[" + result + "]";
	}

	private void businessLogic(Message m) {
		
		Connection conn = null;
		Session session = null;

		try {
			TextMessage tm = (TextMessage) m;

			String text = tm.getText();
			logger.info("message " + text + " received");

			// flip the string
			String result = "";
			for (int i = 0; i < text.length(); i++) {
				result = text.charAt(i) + result;
			}

			logger.info("message processed, result: " + result);

			InitialContext ic = new InitialContext();
			ConnectionFactory cf = (ConnectionFactory) ic.lookup("java:/JmsXA");
			ic.close();

			conn = cf.createConnection();
			conn.start();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Destination replyTo = m.getJMSReplyTo();
			logger.info("Message bundled destination: " + replyTo.toString());
			
			MessageProducer producer = session.createProducer(replyTo);
			TextMessage reply = session.createTextMessage(result);

			producer.send(reply);
			producer.close();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("The Message Driven Bean failed!");
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					logger.error("Could not close the connection!" + e);
				}
			}
		}
	}
	
	public void closeConnection(Connection con) throws JMSException {

		try {
			con.close();

		} catch (JMSException jmse) {
			logger.error("Could not close connection " + con +" exception was " +jmse);
			throw jmse;
		}
	}
}
