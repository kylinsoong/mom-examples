package com.kylin.jms.helloworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.kylin.jms.helloworld.resource.Resource;

public class HelloWorlsJMS {
	
	private final static Logger logger = Logger.getLogger(HelloWorlsJMS.class);
	
	private Context getContext() throws NamingException{
				
		 final Properties env = new Properties();
         env.put(Context.INITIAL_CONTEXT_FACTORY, Resource.get("INITIAL_CONTEXT_FACTORY"));
         env.put(Context.PROVIDER_URL, Resource.get("PROVIDER_URL"));
         env.put(Context.SECURITY_PRINCIPAL, Resource.get("SECURITY_PRINCIPAL"));
         env.put(Context.SECURITY_CREDENTIALS, Resource.get("SECURITY_CREDENTIALS"));
				
		return new InitialContext(env);
	}
	
	public void test() throws NamingException, JMSException {
		
		logger.info("JMS HelloWorld start");
		
		Context ctx = getContext();
		
		logger.info("Create Local JNDI Context Successful");
		
		
		ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
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
            producer = session.createProducer(destination);
            logger.info("create producer successful");
            consumer = session.createConsumer(destination);
            logger.info("create consumer successful");
            connection.start();
            
            int count = Integer.parseInt(Resource.get("DEFAULT_MESSAGE_COUNT"));
            String content = Resource.get("DEFAULT_MESSAGE");
            
            if(Resource.get("LARGE_MESSAGE").equals("true")) {
            	content = getContent();
            }

            logger.info("Sending " + count + " messages with content: " + content);
            
            // Send the specified number of messages
            for (int i = 0; i < count; i++) {
                message = session.createTextMessage(content);
                producer.send(message);
            }

            // Then receive the same number of messaes that were sent
            for (int i = 0; i < count; i++) {
                message = (TextMessage) consumer.receive(5000);
                logger.info("Received message " + (i + 1) + " with content [" + message.getText() + "]");
            }

			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

            // closing the connection takes care of the session, producer, and consumer
            if (connection != null) {
                connection.close();
            }
        }
		
	}

	private String getContent() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(new File("message.xml")));
		
		StringBuffer sb = new StringBuffer();
		
		String buffer;
		while ((buffer = br.readLine()) != null) {
			sb.append(buffer);
		}
		
		return sb.toString();
	}

}
