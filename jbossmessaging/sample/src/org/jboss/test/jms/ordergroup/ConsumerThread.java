package org.jboss.test.jms.ordergroup;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Destination;

public class ConsumerThread extends Thread {
	
	private OrderingGroupTest example;
	private Session session;
	private MessageConsumer consumer;
	private Connection connection;

	public ConsumerThread(String name, OrderingGroupTest theExample, ConnectionFactory fact, Destination queue) throws JMSException {
		super(name);
		example = theExample;

		connection = fact.createConnection();
		session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		consumer = session.createConsumer(queue);
		connection.start();
   }

	public void delay(long nt) {
		try {
			Thread.sleep(nt);
		} catch (InterruptedException e) {
		}
	}

   //receiving the messages
	public void run() {
		int n = example.getNumMessages();
		try {
			while (true) {
				if (example.allReceived()) {
					break;
				}
				TextMessage msg = (TextMessage)consumer.receive(2000);
				if (msg != null) {
					if (msg.getText().equals(OrderingGroupTest.ORDERING_MSG1)) {
						// whoever receives first message, delay for 2 sec.
						delay(2000);
					}
					example.reportReceive(msg);
					msg.acknowledge();
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				try {
					connection.close();
				} catch (JMSException e) {
				}
			}
		}
	}

}
