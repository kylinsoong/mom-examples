package org.jboss.test.jms.common;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;

import org.jboss.security.client.SecurityClient;
import org.jboss.security.client.SecurityClientFactory;


public class Util {

   public static boolean doesDestinationExist(String jndiName) throws Exception {
      
	   return doesDestinationExist(jndiName, null);
   }

   public static boolean doesDestinationExist(String jndiName, InitialContext ic) throws Exception {
     
		if (ic == null) {
			ic = new InitialContext();
		}
		
		try {
			ic.lookup(jndiName);
		} catch (NameNotFoundException e) {
			return false;
		}
		return true;
	}

   public static void deployQueue(String jndiName) throws Exception {
	   
       deployQueue(jndiName,null);
   }

   public static void deployQueue(String jndiName, InitialContext ic) throws Exception {
	   
      MBeanServerConnection mBeanServer = lookupMBeanServerProxy(ic);

      ObjectName serverObjectName = new ObjectName("jboss.messaging:service=ServerPeer");

      String queueName = jndiName.substring(jndiName.lastIndexOf('/') + 1);

      mBeanServer.invoke(serverObjectName, "deployQueue",
                         new Object[] {queueName, jndiName},
                         new String[] {"java.lang.String", "java.lang.String"});

      System.out.println("Queue " + jndiName + " deployed");
   }

   public static void undeployQueue(String jndiName) throws Exception  {
	   
       undeployQueue(jndiName,null);
   }
   
   public static void deployTopic(String jndiName, InitialContext ic) throws Exception {
	   
      MBeanServerConnection mBeanServer = lookupMBeanServerProxy(ic);

      ObjectName serverObjectName = new ObjectName("jboss.messaging:service=ServerPeer");

      String topicName = jndiName.substring(jndiName.lastIndexOf('/') + 1);

      mBeanServer.invoke(serverObjectName, "deployTopic",
                         new Object[] {topicName, jndiName},
                         new String[] {"java.lang.String", "java.lang.String"});

      System.out.println("Topic " + jndiName + " deployed");
   }

   public static void undeployTopic(String jndiName) throws Exception {
	   
       undeployTopic(jndiName,null);
   }

   public static void undeployQueue(String jndiName, InitialContext ic) throws Exception {
	   
      MBeanServerConnection mBeanServer = lookupMBeanServerProxy(ic);

      ObjectName serverObjectName = new ObjectName("jboss.messaging:service=ServerPeer");

      String queueName = jndiName.substring(jndiName.lastIndexOf('/') + 1);

      mBeanServer.invoke(serverObjectName, "destroyQueue",
                         new Object[] {queueName},
                         new String[] {"java.lang.String"});

      System.out.println("Queue " + jndiName + " undeployed");
   }

   public static void undeployTopic(String jndiName, InitialContext ic) throws Exception {
	   
      MBeanServerConnection mBeanServer = lookupMBeanServerProxy(ic);

      ObjectName serverObjectName = new ObjectName("jboss.messaging:service=ServerPeer");

      String topicName = jndiName.substring(jndiName.lastIndexOf('/') + 1);

      mBeanServer.invoke(serverObjectName, "destroyTopic",
                         new Object[] {topicName},
                         new String[] {"java.lang.String"});

      System.out.println("Topic " + jndiName + " undeployed");
   }

   public static MBeanServerConnection lookupMBeanServerProxy(InitialContext ic) throws Exception {
    
		if (ic == null) {
			ic = new InitialContext();
		}
		
		return (MBeanServerConnection)ic.lookup("jmx/invoker/RMIAdaptor");
   }

 
}
