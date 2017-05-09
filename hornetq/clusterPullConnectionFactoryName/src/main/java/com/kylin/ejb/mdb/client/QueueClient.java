package com.kylin.ejb.mdb.client;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;

public class QueueClient extends ClientBase {

	public void test() {

		TextMessage textMsg;
		Connection conn = null;
		Session session = null;
		MessageProducer producer = null;

		try {
			Context context = getContext();
			ConnectionFactory factory = (ConnectionFactory) context .lookup("/ConnectionFactory");

			Queue queue = (Queue) context.lookup("/queue/testQueue");

			Connection connection = factory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			producer = session.createProducer(queue);

			for(int i = 0 ; i < 50 ; i ++) {
				textMsg = session.createTextMessage("" + i);
				producer.send(textMsg);
				System.out.println("Send: "+ textMsg.getText());
			}

			System.out.println(QueueClient.class.getSimpleName() + " DONE");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (producer != null) {
				try {
					producer.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
			if (session != null) {
				try {
					session.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static void main(String[] args) {

		new QueueClient().test();
	}

}
