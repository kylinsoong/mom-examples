package org.jboss.test.jms.topic;

import javax.jms.Message;
import javax.jms.MessageListener;


public class TestListener implements MessageListener {   
	
   private Message message;
      
	public synchronized void onMessage(Message message) {
		
      this.message = message;
      notifyAll();
   }
   
	public synchronized Message getMessage() {
      return message;
   }
      
	protected synchronized void waitForMessage() {
		
		if (message != null) {
			return;
		}

		try {
			wait(5000);
		} catch (InterruptedException e) {
			// OK
		}
	}
}
