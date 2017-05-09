package com.kylin.ejb.mdb;


import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.log4j.Logger;

@MessageDriven(name = "QueueMDBTest", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/testQueue"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class TestQueueMDB extends MDBTestBase implements MessageListener {
	
	private final static Logger logger = Logger.getLogger(TestQueueMDB.class);

	public void onMessage(Message msg) {
		handle(msg);
		log();
	}

	public void log() {

	}

}
