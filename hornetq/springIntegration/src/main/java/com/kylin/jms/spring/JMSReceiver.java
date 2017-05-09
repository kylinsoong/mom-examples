package com.kylin.jms.spring;

public class JMSReceiver {

	public static void main(String[] args) {
		String str = JMSSpringIntegrationFactory.getInstance().getJmsExecuteTemplate().receiveQueueString();
		System.out.println(str);
	}

}
