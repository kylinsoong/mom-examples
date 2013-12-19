package org.jboss.test.jms.client;

import javax.naming.InitialContext;

import org.jboss.test.jms.common.TestBase;
import org.jboss.test.jms.stateless.StatelessTestService;

/**
 * This example deploys a simple Stateless Session Bean that is used as a proxy
 * to send and receive JMS messages in a managed environment.
 * 
 * Since this example is also used by the smoke test, it is essential that the
 * VM exits with exit code 0 in case of successful execution and a non-zero
 * value on failure.
 */
public class EJB3Client extends TestBase {

	public void test() throws Exception {

		InitialContext ic = new InitialContext();
		
		StatelessTestService test = (StatelessTestService) ic.lookup("ejb/StatelessTestService");
		
		String queueName = getDestinationJNDIName();
		
		String text = "Hello!";
		
		test.drain(queueName);
		
		test.send(text, queueName);
		
		log("The " + text + " message was successfully sent to the " + queueName + " queue");
		
		int num = test.browse(queueName);
		assertEquals(1, num);
		log("Queue browse result: " + num);

		String result = test.receive(queueName);
		log("Received " + result);

		assertEquals("Hello!", result);
	}

	protected boolean isQueueTest() {
		return true;
	}

	public static void main(String[] args) {
		new EJB3Client().run();
	}
}
