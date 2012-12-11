package com.kylin.jms.util;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.kylin.jms.helloworld.resource.Resource;

public class JMSSender extends UtilBase{
	
	private final static Logger logger = Logger.getLogger(JMSSender.class);
	
	public void send() throws Exception {
		
		logger.info("JMS Sender start");
		
		Context ctx = getContext();
		
		logger.info("Create Local JNDI Context Successful");
		
		
		ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        Destination destination = null;
        TextMessage message = null;
		
		try {
			String connectionFactoryString = Resource.get("DEFAULT_CONNECTION_FACTORY");
			logger.info("Attempting to acquire connection factory \"" + connectionFactoryString + "\"");
			connectionFactory = (ConnectionFactory) ctx.lookup(connectionFactoryString);
			logger.info("Found connection factory \"" + connectionFactoryString + "\" in JNDI");
			
			String destinationString = Resource.get("DEFAULT_DESTINATION");
			logger.info("Attempting to acquire destination \"" + destinationString + "\"");
            destination = (Destination) ctx.lookup(destinationString);
            logger.info("Found destination \"" + destinationString + "\" in JNDI");
            
            // Create the JMS connection, session, producer, and consumer
            connection = connectionFactory.createConnection(Resource.get("DEFAULT_USERNAME"), Resource.get("DEFAULT_PASSWORD"));
            logger.info("create Connection Factory successful");
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            logger.info("create producer successful");
            connection.start();
            
            int count = Integer.parseInt(Resource.get("DEFAULT_MESSAGE_COUNT"));
  
            // Send the specified number of messages
            for (int i = 0; i < count; i++) {
            	String msgStr = "message-" +i ;
                message = session.createTextMessage(msgStr);
                producer.send(message);
                logger.info("Send Message: " + msgStr);
            }

	
		} catch (Exception e) {
			throw e;
		} finally {

			if(producer != null) {
				producer.close();
			}
			
			if(session != null) {
				session.close();
			}

            if (connection != null) {
                connection.close();
            }
        }
		
	}

	public static void main(String[] args) throws Exception {
		
		new JMSSender().send();
	}
}
