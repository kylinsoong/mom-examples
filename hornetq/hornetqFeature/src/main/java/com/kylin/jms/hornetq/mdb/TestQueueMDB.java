package com.kylin.jms.hornetq.mdb;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

import org.apache.log4j.Logger;

@MessageDriven(name = "TestQueueMDB", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/testQueue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class TestQueueMDB implements MessageListener {

	private final static Logger logger = Logger.getLogger(TestQueueMDB.class);
	
	private static int COUNT = 0;

	public void onMessage(Message msg) {
		
		TextMessage tmsg = (TextMessage) msg;

		try {
			logger.info("handle message [" + tmsg.getText() + "], count = " + COUNT++);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		throw new RuntimeException("redeliver test exception");
	}

}
