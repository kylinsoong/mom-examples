package org.jboss.amq.samples.master;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQSslConnectionFactory;

public class MasterSlaveSslSender {

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
