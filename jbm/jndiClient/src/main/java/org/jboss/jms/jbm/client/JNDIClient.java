package org.jboss.jms.jbm.client;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class JNDIClient {

	public static void main(String[] args) throws NamingException {

		final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        env.put(Context.PROVIDER_URL, "jnp://127.0.0.1:1099");
        env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        
        Context ctx = new InitialContext(env);
        
        System.out.println(ctx);
	}

}
