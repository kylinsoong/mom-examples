package org.jboss.jms.connection;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ConnectionFactoryTest {

	public static void main(String[] args) throws NamingException, JMSException {
		
		final Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		env.put(Context.PROVIDER_URL, "remote://localhost:4447");
		env.put(Context.SECURITY_PRINCIPAL, "jmsclient");
		env.put(Context.SECURITY_CREDENTIALS, "password1!");
		
		Context ctx = new InitialContext(env);
		
		ConnectionFactory connectionFactory = (ConnectionFactory) ctx.lookup("jms/RemoteConnectionFactory");
		Connection connection = connectionFactory.createConnection();
		
		System.out.println(connection);
		
	}

}
