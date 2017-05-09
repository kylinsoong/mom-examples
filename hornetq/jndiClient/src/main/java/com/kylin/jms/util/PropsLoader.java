package com.kylin.jms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class PropsLoader {
		
	private static Map map = null;
	
	public static String get(String key)  {
		
		if(null == map) {
			try {
				initMap();
			} catch (IOException e) {
				throw new RuntimeException("", e);
			}
		}
		
		return (String) map.get(key);
	}

	private static void initMap() throws IOException {
		
		InputStream in = new FileInputStream(new File("configuration.properties"));

		Properties props = new Properties();
		props.load(in);
		map = props ;

	}
	
	public static void main(String[] args) throws IOException {
		
		System.out.print(PropsLoader.get("DEFAULT_MESSAGE"));
	}

}
