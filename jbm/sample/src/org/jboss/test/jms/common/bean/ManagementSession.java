package org.jboss.test.jms.common.bean;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.RemoteBinding;


@Stateless
@Remote(ManagementService.class)
@Local(ManagementServiceLocal.class)
@RemoteBinding(jndiBinding = "/ejb/ManagementService")

public class ManagementSession implements ManagementServiceLocal {
	
	public void killAS() throws Exception {
		
		System.out.println("######");
		System.out.println("######");
		System.out.println("######");
		System.out.println("######");
		System.out.println("###### SIMULATING A FAILURE, KILLING THE VM!");
		System.out.println("######");
		System.out.println("######");
		System.out.println("######");
		System.out.println("######");

		Runtime.getRuntime().halt(1);
	}

	
}
