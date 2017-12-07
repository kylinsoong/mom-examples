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
package org.jboss.amq.samples;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * @author kylin
 */
public class SimplePing {


	public static void main(String[] args) throws JMSException {

		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory();
		factory.setBrokerURL("tcp://localhost:61616");
		factory.setUserName("admin");
		factory.setPassword("admin");
		Connection conn = factory.createConnection();
		conn.start();
		
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = session.createQueue("testQueue");
		MessageProducer messageProducer = session.createProducer(queue);
		MessageConsumer messageConsumer = session.createConsumer(queue);

        TextMessage message = session.createTextMessage("PING JBOSS AMQ 6.3 SUCCESS!");
        messageProducer.send(message, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
        TextMessage receivedMessage = (TextMessage) messageConsumer.receive(5000L);

        if (receivedMessage != null) {
            System.out.println(receivedMessage.getText());
        } else {
            System.out.println("No message received within the given timeout!");
        }
		
		session.close();
		conn.close();
	}

}
