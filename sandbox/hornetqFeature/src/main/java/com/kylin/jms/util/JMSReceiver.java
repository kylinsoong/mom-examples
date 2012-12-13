package com.kylin.jms.util;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;

import org.apache.log4j.Logger;

import com.kylin.jms.resource.Resource;

public class JMSReceiver extends UtilBase {
	
	private final static Logger logger = Logger.getLogger(JMSReceiver.class);

	public static void main(String[] args) throws Exception {
		new JMSReceiver().recieve();
	}

	private void recieve() throws Exception {

	logger.info("JMS Receiver start");
		
		Context ctx = getContext();
		
		logger.info("Create Local JNDI Context Successful");
		
		
		ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
//        MessageProducer producer = null;
        MessageConsumer consumer = null;
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
            consumer = session.createConsumer(destination);
            logger.info("create consumer successful");
            connection.start();
            
            int count = Integer.parseInt(Resource.get("DEFAULT_MESSAGE_COUNT"));
  
            // Send the specified number of messages
            for (int i = 0; i < count; i++) {
            	message = (TextMessage) consumer.receive(5000);
                logger.info("Received message " + (i + 1) + " with content [" + message.getText() + "]");
            }

	
		} catch (Exception e) {
			throw e;
		} finally {

			if(consumer != null) {
				consumer.close();
			}
			
			if(session != null) {
				session.close();
			}

            if (connection != null) {
                connection.close();
            }
        }
	}

}
