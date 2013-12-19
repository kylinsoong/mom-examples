package org.jboss.test.jms.client;

import javax.naming.InitialContext;

import org.jboss.test.jms.common.bean.ManagementService;

public class ManagementServiceClient {

	public static void main(String[] args) throws Exception {
		
		InitialContext ic = new InitialContext();
		ManagementService service = (ManagementService)ic.lookup("ejb/ManagementService");
		service.killAS();
	}

}
