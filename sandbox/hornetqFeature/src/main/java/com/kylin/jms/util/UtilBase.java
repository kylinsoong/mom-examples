package com.kylin.jms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.kylin.jms.resource.Resource;

public class UtilBase {

	protected Context getContext() throws NamingException{
		
		 final Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, Resource.get("INITIAL_CONTEXT_FACTORY"));
        env.put(Context.PROVIDER_URL, Resource.get("PROVIDER_URL"));
        env.put(Context.SECURITY_PRINCIPAL, Resource.get("SECURITY_PRINCIPAL"));
        env.put(Context.SECURITY_CREDENTIALS, Resource.get("SECURITY_CREDENTIALS"));
				
		return new InitialContext(env);
	}
	
	protected String getContent() throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(new File("message.xml")));
		
		StringBuffer sb = new StringBuffer();
		
		String buffer;
		while ((buffer = br.readLine()) != null) {
			sb.append(buffer);
		}
		
		return sb.toString();
	}
}
