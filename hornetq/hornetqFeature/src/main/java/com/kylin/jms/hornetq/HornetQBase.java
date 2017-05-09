package com.kylin.jms.hornetq;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;

import javax.jms.Connection;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.api.jms.JMSFactoryType;
import org.hornetq.core.client.impl.DelegatingSession;
import org.hornetq.core.remoting.impl.netty.NettyConnectorFactory;
import org.hornetq.jms.client.HornetQConnection;
import org.hornetq.jms.client.HornetQConnectionFactory;

public abstract class HornetQBase {

	protected static final Logger log = Logger.getLogger(HornetQBase.class);

	protected boolean failure = false;

	private String[] args;

	public abstract boolean run() throws Exception;

	protected void run(final String[] args1) {
		
		this.args = args1;
		// if we have a cluster of servers wait a while for the cluster to form
		// properly
		if (args != null && args.length > 1) {
			System.out.println("****pausing to allow cluster to form****");
			Thread.yield();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// ignore
			}
		}

		try {
			if (!run()) {
				failure = true;
			}
			System.out.println("DONE");
		} catch (Throwable e) {
			failure = true;
			e.printStackTrace();
		}
		reportResultAndExit();
	}
	
	protected void killServer(final int id) throws Exception {
		
		String configDir = System.getProperty("hornetqfeature.configDir");
		if (configDir == null) {
			throw new Exception("hornetqfeature.configDir must be set as a system property");
		}

		System.out.println("Killing server " + id);

		// We kill the server by creating a new file in the server dir which is
		// checked for by the server
		// We can't use Process.destroy() since this does not do a hard kill -
		// it causes shutdown hooks
		// to be called which cleanly shutdown the server
		System.out.println(configDir + "/server" + id + "/KILL_ME");
		File file = new File(configDir + "/server" + id + "/KILL_ME");

		file.createNewFile();

		// Sleep longer than the KillChecker check period
		Thread.sleep(3000);
	}
	
	protected void killServer(final int id, final int serverToWaitFor) throws Exception {
		
		String configDir = System.getProperty("hornetqfeature.configDir");
		if (configDir == null) {
			throw new Exception("hornetqfeature.configDir must be set as a system property");
		}

		System.out.println("Killing server " + id);

		// We kill the server by creating a new file in the server dir which is
		// checked for by the server
		// We can't use Process.destroy() since this does not do a hard kill -
		// it causes shutdown hooks
		// to be called which cleanly shutdown the server
		System.out.println(configDir + "/server" + id + "/KILL_ME");
		File file = new File(configDir + "/server" + id + "/KILL_ME");

		file.createNewFile();

		waitForServerStart(serverToWaitFor, 20000);
	}
	
	protected void reStartServer(final int id, final long timeout) throws Exception {
		
		String configDir = System.getProperty("hornetqfeature.configDir");
		if (configDir == null) {
			throw new Exception("hornetqfeature.configDir must be set as a system property");
		}

		System.out.println("restarting server " + id);

		// We kill the server by creating a new file in the server dir which is
		// checked for by the server
		// We can't use Process.destroy() since this does not do a hard kill -
		// it causes shutdown hooks
		// to be called which cleanly shutdown the server
		File file = new File(configDir + "/server" + id + "/RESTART_ME");

		file.createNewFile();

		waitForServerStart(id, timeout);
	}
	
	protected InitialContext getContext(final int serverId) throws Exception {
		
		HornetQBase.log.info("using " + args[serverId] + " for jndi");
		
		Properties props = new Properties();
		props.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
		props.put("java.naming.provider.url", args[serverId]);
		props.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
		return new InitialContext(props);
	}
	
	protected int getServer(Connection connection) {
		
		DelegatingSession session = (DelegatingSession) ((HornetQConnection) connection).getInitialSession();
		TransportConfiguration transportConfiguration = session.getSessionFactory().getConnectorConfiguration();
		String port = (String) transportConfiguration.getParams().get("port");
		return Integer.valueOf(port) - 5445;
	}

	protected Connection getServerConnection(int server, Connection... connections) {
		
		for (Connection connection : connections) {
			DelegatingSession session = (DelegatingSession) ((HornetQConnection) connection).getInitialSession();
			TransportConfiguration transportConfiguration = session.getSessionFactory().getConnectorConfiguration();
			String port = (String) transportConfiguration.getParams().get("port");
			if (Integer.valueOf(port) == server + 5445) {
				return connection;
			}
		}
		return null;
	}
	
	private void waitForServerStart(int id, long timeout) throws InterruptedException {
		
		// wait for restart
		long time = System.currentTimeMillis();
		while (time < System.currentTimeMillis() + timeout) {
			try {
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("host", "localhost");
				params.put("port", 5445 + id);
				TransportConfiguration transportConfiguration = new TransportConfiguration(NettyConnectorFactory.class.getName(), params);
				HornetQConnectionFactory cf = HornetQJMSClient.createConnectionFactoryWithoutHA(JMSFactoryType.CF, transportConfiguration);
				cf.createConnection().close();
				System.out.println("server restarted");
			} catch (Exception e) {
				System.out.println("awaiting server restart");
				Thread.sleep(1000);
				continue;
			}
			break;
		}
	}

	private void reportResultAndExit() {
		
		if (failure) {
			System.err.println();
			System.err.println("#####################");
			System.err.println("### FAILURE! ###");
			System.err.println("#####################");
			System.exit(1);
		} else {
			System.out.println();
			System.out.println("#####################");
			System.out.println("### SUCCESS! ###");
			System.out.println("#####################");
		}
	}
}
