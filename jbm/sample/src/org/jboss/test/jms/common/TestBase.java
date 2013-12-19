package org.jboss.test.jms.common;

import javax.jms.Connection;
import javax.jms.ConnectionMetaData;
import javax.naming.InitialContext;

import org.jboss.jms.client.JBossConnection;
import org.jboss.jms.client.delegate.DelegateSupport;
import org.jboss.jms.client.state.ConnectionState;
import org.jboss.security.client.SecurityClient;
import org.jboss.security.client.SecurityClientFactory;
import org.jboss.test.jms.common.bean.ManagementService;

public abstract class TestBase {
	
	static {
		
		try {
			SecurityClient client = SecurityClientFactory.getSecurityClient();
			client.setSimple("admin", "admin");
			client.login();
		}  catch (Exception e) {
			e.printStackTrace();
		}
		
	}
   
   public static final String DEFAULT_QUEUE_NAME = "testQueue";
   public static final String DEFAULT_TOPIC_NAME = "testTopic";
   
   
   public static int getServerID(Connection conn) throws Exception {
	   
		if (!(conn instanceof JBossConnection)) {
			throw new Exception("Connection not an instance of JBossConnection");
		}

		JBossConnection jbconn = (JBossConnection) conn;

		DelegateSupport del = (DelegateSupport) jbconn.getDelegate();

		ConnectionState state = (ConnectionState) del.getState();

		return state.getServerID();
	}
   
   public static void assertEquals(Object o, Object o2) {
	   
		if (o == null && o2 == null) {
			return;
		}

		if (o.equals(o2)) {
			return;
		}
      
		throw new RuntimeException("Assertion failed, " + o + " != " + o2);
	}
   
	public static void assertEquals(int i, int i2) {
		
		if (i == i2) {
			return;
		}
      
		throw new RuntimeException("Assertion failed, " + i + " != " + i2);
	}
   
	public static void assertNotEquals(int i, int i2) {
      
		if (i != i2) {
			return;
		}
      
		throw new RuntimeException("Assertion failed, " + i + " == " + i2);
	}
   
   
   public static void killActiveNode() throws Exception {
      
		try {
			
			InitialContext ic = new InitialContext();
         
			ManagementService service = (ManagementService)ic.lookup("ejb/ManagementService");

			try {
				service.killAS();
			} catch (Exception e) {
				// OK, I expect exceptions following a VM kill
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not kill the active node", e);
		}
	}
      
	private boolean failure;
	private boolean deployed;
	private String jndiDestinationName;
	private String jndiDestinationName2;
      
	protected TestBase() {
		failure = false;
	}
 
	protected abstract void test() throws Exception;

	protected abstract boolean isQueueTest();
   
	protected final boolean isTopicTest() {
		return !isTopicTest();
	}
   
	protected void run() {
		try {
         setup();
         test();
         tearDown();
		} catch (Throwable t) {
			t.printStackTrace();
			System.out.println("");
			System.out.println("Please verify if you have access to the server. If you are using JBossEAP maybe you don't have security access");
			setFailure(true);
		}

		reportResultAndExit();
	}
   
	protected void setFailure(boolean b) {
		failure = b;
	}

	protected boolean isFailure() {
		return failure;
	}
   
	protected String getDestinationJNDIName() {
      return jndiDestinationName;
   }
   
	protected void log(String s) {
      System.out.println(s);
   }
   
   protected void displayProviderInfo(ConnectionMetaData metaData) throws Exception {
	   
		String info =
         "The example connected to " + metaData.getJMSProviderName() +
         " version " + metaData.getProviderVersion() + " (" +
         metaData.getProviderMajorVersion() + "." + metaData.getProviderMinorVersion() +
         ")";
      
		System.out.println(info);
	}
   
   
	protected void setup() throws Exception {
		
		setup(null);
	}
   
	protected void setup(InitialContext ic) throws Exception {
      
		String destinationName = null;
		String destinationName2 = null;

		if (isQueueTest()) {
			
			destinationName = System.getProperty("example.queue.name");
		 
			if (destinationName == null) {
				
				destinationName = System.getProperty("example.source.queue");
				
				if (destinationName != null) {
					destinationName2 = System.getProperty("example.target.queue");
					
					if (destinationName2 != null) {
						jndiDestinationName2 = "/queue/" + destinationName2;
					}
					
				} else {
					destinationName = DEFAULT_QUEUE_NAME;
				}
			}
		 
			jndiDestinationName = "/queue/" + destinationName;
			
		} else {
			
			destinationName = System.getProperty("example.topic.name");
			jndiDestinationName = "/topic/"  + (destinationName == null ? DEFAULT_TOPIC_NAME : destinationName);
      }
      
		if (!Util.doesDestinationExist(jndiDestinationName, ic)) {
			
			System.out.println("Destination " + jndiDestinationName + " does not exist, deploying it");

			if (isQueueTest()) {
				
				Util.deployQueue(jndiDestinationName, ic);
				
				if (jndiDestinationName2 != null) {
					Util.deployQueue(jndiDestinationName2, ic);
				}
			} else {
				Util.deployTopic(jndiDestinationName, ic);
			}
			deployed = true;
		}
	}
   
	protected void tearDown() throws Exception {
		tearDown(null);
	}
   
	protected void tearDown(InitialContext ic) throws Exception {
		
		if (deployed) {
			Util.undeployQueue(jndiDestinationName, ic);
			if (jndiDestinationName2 != null) {
				Util.undeployQueue(jndiDestinationName2);
			}
		}
	}
   
	protected void reportResultAndExit() {
		
		if (isFailure()) {
			System.err.println();
			System.err.println("#####################");
			System.err.println("###    FAILURE!   ###");
			System.err.println("#####################");
			System.exit(1);
		}
      
		System.out.println();
		System.out.println("#####################");
		System.out.println("###    SUCCESS!   ###");
		System.out.println("#####################");
		System.exit(0);
	}

}
