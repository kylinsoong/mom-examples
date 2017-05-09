package com.kylin.ejb.mdb;


import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

public abstract class MDBTestBase {
	
	private final static Logger logger = Logger.getLogger(MDBTestBase.class);
	
	public void handle(Message rcvMessage) {
				
		TextMessage msg = null;
		try {
			if (rcvMessage instanceof TextMessage) {
				msg = (TextMessage) rcvMessage;
				logger.info("Received Message: " + msg.getText());
			} else {
				logger.warn("Message of wrong type: " + rcvMessage.getClass().getName());
			}
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
	

	public abstract void log();

}
