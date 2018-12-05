package org.jboss.amq.samples;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Main {

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
        messageProducer.setPriority(6);
        
        for (int i = 0 ; i < 3 ; i ++) {
            TextMessage message = session.createTextMessage("PING");
            message.setJMSPriority(6);
            messageProducer.send(message);
            messageProducer.send(message, 2, 6, 2000);
            System.out.println("SEND MSG");
        }
        
        session.close();
        conn.close();
    }

}
