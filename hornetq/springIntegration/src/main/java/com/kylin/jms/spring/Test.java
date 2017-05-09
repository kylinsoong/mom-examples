package com.kylin.jms.spring;

public class Test {

	public static void main(String[] args) {

		JMSSpringIntegrationFactory.getInstance().getHelloWorldBean().helloworld();
		
		JMSSpringIntegrationFactory.getInstance().getJmsExecuteTemplate().sendQueueString("Hello World");
	}

}
