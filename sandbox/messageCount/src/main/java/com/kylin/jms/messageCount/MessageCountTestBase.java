package com.kylin.jms.messageCount;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.kylin.jms.messageCount.resource.Resource;

public class MessageCountTestBase {
	
	private final static Logger logger = Logger.getLogger(MessageCountTestBase.class);
	
	protected String queueName = "queue/testQueue";
	
	protected ConnectionFactory connectionFactory = null;
	
	protected Connection connection = null;
	
	protected Session session = null;
	
	protected Queue destination = null;
	
	public MessageCountTestBase() {
		
		System.out.println("initial JMS Session, Connection, ConnectionFactory");
		
		try {
			connectionFactory = (ConnectionFactory) getContext().lookup("ConnectionFactory");
			destination = (Queue) getContext().lookup("queue/testQueue");
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private Context getContext() throws NamingException{
		
		final Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, Resource.get("INITIAL_CONTEXT_FACTORY"));
		env.put(Context.PROVIDER_URL, Resource.get("PROVIDER_URL"));
		env.put(Context.URL_PKG_PREFIXES, Resource.get("PACKAGE_NAME"));
        
		return new InitialContext(env);
	}
}
