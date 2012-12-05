package com.kylin.jms.hornetQ;

public class TaskClientTest {

	public static void main(String[] args) {

		TaskClientConnector task = new HornetQTaskClientConnector("test");
		
		task.connect("127.0.0.1", 5153);
	}

}
