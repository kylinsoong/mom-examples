package com.kylin.jms.spring;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;



public class JmsExecuteTemplate {

	private JmsTemplate jmsQueueTemplate;
	
	private JmsTemplate jmsListenerTemplate;

	public JmsTemplate getJmsQueueTemplate() {
		return jmsQueueTemplate;
	}

	public void setJmsQueueTemplate(JmsTemplate jmsQueueTemplate) {
		this.jmsQueueTemplate = jmsQueueTemplate;
	}
	
	public JmsTemplate getJmsListenerTemplate() {
		return jmsListenerTemplate;
	}

	public void setJmsListenerTemplate(JmsTemplate jmsListenerTemplate) {
		this.jmsListenerTemplate = jmsListenerTemplate;
	}

	public void sendQueueString(final String message) {
		jmsQueueTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		});
	}
	
	public void sendQueueString(String destination ,final String message) {
		jmsQueueTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		});
	}
	
	public String receiveQueueString() {
		TextMessage textMessage = (TextMessage) jmsQueueTemplate.receive();
		String message = null;
		try {
			message = textMessage.getText();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return message;
	}
}
