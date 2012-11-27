package com.kylin.jms.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class JMSSpringIntegrationFactory {

	private JMSSpringIntegrationFactory() {
		
	}
	
	private static JMSSpringIntegrationFactory instance;
	
	public static JMSSpringIntegrationFactory getInstance() {
		
		if(null == instance) {
			instance = new JMSSpringIntegrationFactory();
		}
		
		return instance;
	}
	
	private ApplicationContext ctx;
	
	private ApplicationContext getApplicationContext() {
		
		if(null == ctx) {
			ctx = new FileSystemXmlApplicationContext("jms-spring.xml");
		}
		return ctx;
	}
	
	public HelloWorldBean getHelloWorldBean() {
		return (HelloWorldBean) getApplicationContext().getBean("helloworldBean"); 
	}
	
	public JmsExecuteTemplate getJmsExecuteTemplate() {
		return (JmsExecuteTemplate) getApplicationContext().getBean("jmsExecuteTemplateBean"); 
	}
	
}
