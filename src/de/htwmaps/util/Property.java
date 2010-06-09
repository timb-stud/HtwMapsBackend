package de.htwmaps.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Name: <code>Property</code>
 * 
 * Hiermit koennen Eigenschaften aus einer Datei gelesen werden
 * <ul>
 * 	 <li>@author CR</li>
 * 	 <li>@version: 1.0 - 26.05.2010</li>
 * </ul>
 */

public class Property {
	private Properties properties;
	
	public Property(String filename){
		properties = new Properties();
		FileInputStream stream = null;
		
		try {
			stream = new FileInputStream("./" + filename.toString());
			properties.load(stream);
			
			if (stream != null)
				stream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String getProp(String name) {
		return properties.getProperty(name);
	}
}