/*
 * JBoss, Home of Professional Open Source
 * Copyright 2017, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.amq.samples.ssl;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQSslConnectionFactory;

/**
 * @author kylin
 */
public class SSLClientSender {

	public static void main(String[] args) throws Exception {
		
		ActiveMQSslConnectionFactory connectionFactory = new ActiveMQSslConnectionFactory();
		connectionFactory.setTrustStore("/home/kylin/work/amq/sinopharm/ssl/spclient.ts");
		connectionFactory.setTrustStorePassword("cmsfe_framework_q1w2e3");
		connectionFactory.setKeyStore("/home/kylin/work/amq/sinopharm/ssl/spclient.ks");
		connectionFactory.setKeyStorePassword("cmsfe_framework_q1w2e3");
		connectionFactory.setBrokerURL("ssl://10.32.8.112:61617");
		connectionFactory.setUserName("cmsfe");
		connectionFactory.setPassword("cmsfe_activemq@123");
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue("testSslQueue");
		
		MessageProducer producer = session.createProducer(destination);
		
		for(int i = 0 ; i < 10 ; i ++) {
			TextMessage message = session.createTextMessage(i + "-#&#&#&&^#&#&&#&#^&#&#^#*^*#*#^#*#*#^*#^*#^#^^#");
			producer.send(message, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
			System.out.println("Send Message " + message.getText());
		}
		
		
		producer.close();
		session.close();
		connection.close();
	}

}
