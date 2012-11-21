package com.kylin.jms.messageCount;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.QueueBrowser;
import javax.jms.TextMessage;

public class MessageCountTestProducer extends MessageCountTestBase {

	public static void main(String[] args) {
		new MessageCountTestProducer().produce();
	}

	private void produce() {
		
		System.out.println("MessageCount Test Producer Start");
		
		MessageProducer producer = null;
		QueueBrowser browser = null;
		
		try {
			producer = session.createProducer(destination);
			browser = session.createBrowser(destination);
			
			int index = 0;
			
			while(true) {
				index ++ ;
				for(int i = 0 ; i < 1000 ; i ++) {
					TextMessage msg = session.createTextMessage((index * 1000) + i + "");
					producer.send(msg);
					System.out.println("Send Test Message " + msg.getText());
				}
				
				while(browser.getEnumeration().hasMoreElements()){
					Thread.sleep(2000);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
