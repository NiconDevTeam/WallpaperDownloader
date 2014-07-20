package es.estoes.wallpaperDownloader.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import es.estoes.wallpaperDownloader.exceptions.WDPropertyException;

/**
 * This class uses the Singleton principle and design pattern. It can only be instantiated 
 * one time during the execution of the application and it gathers all the methods related
 * to the properties of the wallpaper downloader application
 * 
 * @author egarcia
 *
 */
public class PropertiesManager {

	// Constants
	private static volatile PropertiesManager instance;
	private static final Logger LOG = Logger.getLogger(PropertiesManager.class);
	private static final String APP_PROPERTIES_FILE_NAME = "application.properties";
	private static final String LOG4J_PROPERTIES_FILE_NAME = "log4j.properties";
	
	// Atributes
	
	// Getters & Setters	

	// Methods
	/**
	 * Constructor
	 */
	private PropertiesManager () {
	}
	
	public static PropertiesManager getInstance() {
		if (instance == null) {
			synchronized (PropertiesManager.class) {
				if (instance == null) {
					instance = new PropertiesManager();
					
				}
			}
		}
		return instance;
	}
	
	/**
	 * This method gets any property from properties file 
	 * @param property
	 * @return
	 */
	public String getProperty(String property) {
		InputStream input = null;
		Properties prop = new Properties();
		String value = null;
		String resource = null;
		try {
			input = this.getClass().getClassLoader().getResourceAsStream(APP_PROPERTIES_FILE_NAME);
			prop.load(input);
			value = prop.getProperty(property);
		} catch (FileNotFoundException e) {
			throw new WDPropertyException(resource + " properties file wasn't found. Error: " + e.getMessage());
		} catch (IOException e) {
			throw new WDPropertyException("Error loading InputStream for reading properties file. Error: " + e.getMessage());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					throw new WDPropertyException("Error closing InputStream. Error: " + e.getMessage());
				}	
			}		
		}
		return value;
	}
	
	/**
	 * This class sets a log4j property
	 * @param property
	 * @param value
	 */
	public void setLog4jProperty(String property, String value) {
		InputStream input = null;
		Properties log4jProperties = new Properties();
		try {
			input = this.getClass().getClassLoader().getResourceAsStream(LOG4J_PROPERTIES_FILE_NAME);
			log4jProperties.load(input);
			input.close();	
		} catch (FileNotFoundException e) {
			LOG.error("log4j properties file wasn't found. Error: " + e.getMessage());
		} catch (IOException e) {
			LOG.error("Error while loading InputStream for reading properties file. Error: " + e.getMessage());
		}
		
		log4jProperties.setProperty(property, value);
		PropertyConfigurator.configure(log4jProperties);
	}
}
