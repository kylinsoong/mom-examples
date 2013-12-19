package org.jboss.test.jms.stateless;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.RemoteBinding;

@Stateless
@Remote(StatelessTestService.class)
@Local(StatelessTestServiceLocal.class)
@RemoteBinding(jndiBinding = "/ejb/StatelessTestService")
public class StatelessTestSession implements StatelessTestServiceLocal {
	
	private static final Logger logger = Logger.getLogger(StatelessTestSession.class);

	public StatelessTestSession() {
		
		logger.info("StatelessTestSession constructed");
		
		try {
			InitialContext ic = new InitialContext();
			cf = (ConnectionFactory)ic.lookup("java:/JmsXA");
			ic.close();
		} catch (NamingException e) {
			logger.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private ConnectionFactory cf = null;

	public void drain(String queueName) throws Exception {
		
		logger.info("drain message from " + queueName);
		
		InitialContext ic = new InitialContext();
		Queue queue = (Queue) ic.lookup(queueName);
		ic.close();

		Session session = null;
		Connection conn = null;
		int count = 0;
		try {
			conn = getConnection();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = session.createConsumer(queue);
			Message m = null;
			
			do {
				m = consumer.receive(1L);
				if(null != m) {
					count ++ ;
					logger.info("...draining msg: " + m.getJMSCorrelationID());
				}
			} while (m != null);
			
		} finally {
			if (conn != null) {
				closeConnection(conn);
			}
		}
		
		logger.info("drain " + count + " messages");
	}

	public void send(String txt, String queueName) throws Exception {
		
		logger.info("send message starting...");
		
		InitialContext ic = new InitialContext();
		Queue queue = (Queue) ic.lookup(queueName);
		ic.close();

		Session session = null;
		Connection conn = null;

		try {
			conn = getConnection();

			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

			MessageProducer producer = session.createProducer(queue);

			TextMessage tm = session.createTextMessage(txt);

			producer.send(tm);

			logger.info("message " + txt + " sent to " + queueName);
		} finally {
			if (conn != null) {
				closeConnection(conn);
			}
		}
	}

	public int browse(String queueName) throws Exception {
		
		logger.info("browse queue " + queueName);
		
		InitialContext ic = new InitialContext();
		Queue queue = (Queue) ic.lookup(queueName);
		ic.close();

		Session session = null;
		Connection conn = null;

		try {
			conn = getConnection();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueBrowser browser = session.createBrowser(queue);

			ArrayList list = new ArrayList();
			for (Enumeration e = browser.getEnumeration(); e.hasMoreElements();) {
				TextMessage msg = (TextMessage) e.nextElement();
				logger.info("... browser Msg[" + msg.getText() + "], Correlation ID: " + msg.getJMSCorrelationID() + ", Message ID: " + msg.getJMSMessageID());
				list.add(msg);
			}

			return list.size();
		} finally {
			if (conn != null) {
				closeConnection(conn);
			}
		}
	}

	public String receive(String queueName) throws Exception {
		
		logger.info("recieve message from " + queueName);
		
		InitialContext ic = new InitialContext();
		Queue queue = (Queue) ic.lookup(queueName);
		ic.close();

		Session session = null;
		Connection conn = null;

		try {
			conn = getConnection();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

			MessageConsumer consumer = session.createConsumer(queue);

			logger.info("blocking to receive message from queue " + queueName + " ...");
			
			TextMessage tm = (TextMessage) consumer.receive(5000);

			if (tm == null) {
				throw new Exception("No message!");
			}

			logger.info("Message " + tm.getText() + " received");

			return tm.getText();
		} finally {
			if (conn != null) {
				closeConnection(conn);
			}
		}
	}

	public Connection getConnection() throws Exception {

		Connection connection = null;

		try {
			connection = cf.createConnection();

			connection.start();
		} catch (Exception e) {
			
			if (connection != null) {
				closeConnection(connection);
			}
			
			logger.error("Failed to get connection...exception is " + e);
			throw e;
		}

		return connection;
	}

	public void closeConnection(Connection con) throws Exception {
		try {
			con.close();
		} catch (JMSException jmse) {
			logger.error("Could not close connection " + con + " exception was " + jmse);
			throw jmse;
		}
	}

}
