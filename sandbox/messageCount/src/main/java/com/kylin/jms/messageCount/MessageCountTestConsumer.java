package com.kylin.jms.messageCount;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

public class MessageCountTestConsumer extends MessageCountTestBase {

	public static void main(String[] args) {
		new MessageCountTestConsumer().consume();
	}

	private void consume() {
		
		System.out.println("MessageCount Test Consumer Start");

		MessageConsumer consumer = null;
		
		try {
			consumer = session.createConsumer(destination);
			connection.start();
			
			TextMessage msg = null;
			
			while(true) {
				
				try {
					msg = (TextMessage) consumer.receive();
					System.out.println("Receive: " + msg.getText());
				} catch (Exception e) {
					System.out.println("Receive message error: " + e.getMessage());
				}
				
				int num = Integer.parseInt(msg.getText());
				if(num % 3 == 0) {
					Thread.sleep(100);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
