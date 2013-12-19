package org.jboss.test.jms.stateless;

public interface StatelessTestService {

	public void drain(String queueName) throws Exception;

	public void send(String txt, String queueName) throws Exception;

	public int browse(String queueName) throws Exception;

	public String receive(String queueName) throws Exception;
}
