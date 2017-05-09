package com.kylin.jms.util;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Util {

	public static Context getContext() throws NamingException{
		
		final Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY, PropsLoader.get("INITIAL_CONTEXT_FACTORY"));
		env.put(Context.PROVIDER_URL, PropsLoader.get("PROVIDER_URL"));
		env.put(Context.SECURITY_PRINCIPAL, PropsLoader.get("SECURITY_PRINCIPAL"));
		env.put(Context.SECURITY_CREDENTIALS, PropsLoader.get("SECURITY_CREDENTIALS"));
				
		return new InitialContext(env);
	}
}
