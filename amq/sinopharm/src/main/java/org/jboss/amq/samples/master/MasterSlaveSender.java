package org.jboss.amq.samples.master;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class MasterSlaveSender {

	public static void main(String[] args) throws JMSException {

		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL("failover:(tcp://localhost:61616,tcp://localhost:61618)");
		factory.setUserName("admin");
		factory.setPassword("admin");
		Connection conn = factory.createConnection();
		conn.start();
		
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("testClusterQueue");
		
		MessageProducer producer = session.createProducer(destination);
		
		for(int i = 0 ; i < 10 ; i ++) {
			TextMessage message = session.createTextMessage(i + "-#&#&#&&^#&#&&#&#^&#&#^#*^*#*#^#*#*#^*#^*#^#^^#");
			producer.send(message, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
			System.out.println("Send Message " + message.getText());
		}
		
		
		producer.close();
		session.close();
		conn.close();
	}

}
