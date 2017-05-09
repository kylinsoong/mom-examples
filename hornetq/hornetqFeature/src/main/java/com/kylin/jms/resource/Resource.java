package com.kylin.jms.resource;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Resource {
	
	private final static ResourceBundle bundle = ResourceBundle.getBundle("com.kylin.jms.resource.resource");
	
	private static Map<String, String> map = null;
	
	public static String get(String key) {
		
		if(null == map) {
			map = new HashMap<String, String>();
			initMap();
		}
		
		return map.get(key);
	}

	private static void initMap() {

		Enumeration<String> keys = bundle.getKeys();

		String key = null;
		while (keys.hasMoreElements()) {

			key = keys.nextElement();
			
			map.put(key, (String) bundle.getObject(key));
			
		}
	}
	
	public static void main(String[] args) {
		
		Resource.get("");
		
		System.out.print(map);
	}

}
