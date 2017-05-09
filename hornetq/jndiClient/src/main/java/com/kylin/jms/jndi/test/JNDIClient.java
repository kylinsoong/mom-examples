package com.kylin.jms.jndi.test;

import javax.naming.NamingException;

import com.kylin.jms.util.Util;

public class JNDIClient {

	public static void main(String[] args) throws NamingException {
		
		Object obj = Util.getContext().lookup("java:global/remote-server/InfinispanSession!com.kylin.infinispan.custom.InfinispanService");
	}
}
