package com.kylin.jms.hornetQ;

import java.util.concurrent.atomic.AtomicInteger;

public interface TaskClientConnector {
	
	public boolean connect();

	public boolean connect(String address, int port);

	public void disconnect() throws Exception;

	public void write(Object message);

	public String getName();

	public AtomicInteger getCounter();

}
