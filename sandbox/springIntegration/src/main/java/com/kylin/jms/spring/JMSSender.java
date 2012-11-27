package com.kylin.jms.spring;

public class JMSSender {

	public static void main(String[] args) {
		JMSSpringIntegrationFactory.getInstance().getJmsExecuteTemplate().sendQueueString("Hello, World");
		System.out.println("Done");
	}
}
