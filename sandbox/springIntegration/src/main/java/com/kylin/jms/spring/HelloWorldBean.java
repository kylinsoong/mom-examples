package com.kylin.jms.spring;

public class HelloWorldBean {
	
	private String helloworldName;

	

	public String getHelloworldName() {
		return helloworldName;
	}



	public void setHelloworldName(String helloworldName) {
		this.helloworldName = helloworldName;
	}



	public void helloworld() {
		System.out.println("Hello World " + helloworldName);
	}
}
