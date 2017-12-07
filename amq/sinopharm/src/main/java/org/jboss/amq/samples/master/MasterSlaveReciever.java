package org.jboss.amq.samples.master;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class MasterSlaveReciever {

	public static void main(String[] args) throws JMSException {

		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL("failover:(tcp://localhost:61616,tcp://localhost:61618)");
		factory.setUserName("admin");
		factory.setPassword("admin");
		Connection conn = factory.createConnection();
		conn.start();
		
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("testClusterQueue");
		
        MessageConsumer consumer = session.createConsumer(destination);
		
		for(int i = 0 ; i < 10 ; i ++) {
			TextMessage receivedMessage = (TextMessage) consumer.receive(2000L);
			if (receivedMessage != null) {
	            System.out.println("Recieved Message: " + receivedMessage.getText());
	        } else {
	            System.out.println("No message received within the given timeout!");
	        }
		}
		
		TextMessage receivedMessage = (TextMessage) consumer.receive(2000L);
		if (receivedMessage != null) {
            System.out.println(receivedMessage.getText());
        } else {
            System.out.println("No message received within the given timeout!");
        }
		
		consumer.close();
		session.close();
		conn.close();
	}

}
