package org.jboss.amq.samples.master;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQSslConnectionFactory;

public class MasterSlaveSslReciever {

	public static void main(String[] args) throws Exception {

		ActiveMQSslConnectionFactory connectionFactory = new ActiveMQSslConnectionFactory();
		connectionFactory.setTrustStore("/home/kylin/work/amq/sinopharm/ssl/spclient.ts");
		connectionFactory.setTrustStorePassword("cmsfe_framework_q1w2e3");
		connectionFactory.setKeyStore("/home/kylin/work/amq/sinopharm/ssl/spclient.ks");
		connectionFactory.setKeyStorePassword("cmsfe_framework_q1w2e3");
		connectionFactory.setBrokerURL("failover:(ssl://localhost:61617,ssl://localhost:61619)");
		connectionFactory.setUserName("admin");
		connectionFactory.setPassword("admin");
		Connection conn = connectionFactory.createConnection();
		conn.start();
		
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("testClusterSslQueue");
		
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
