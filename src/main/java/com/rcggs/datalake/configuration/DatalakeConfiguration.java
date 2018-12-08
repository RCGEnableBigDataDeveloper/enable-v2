
package com.rcggs.datalake.configuration;

import java.io.File;
import java.io.InputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.inject.Singleton;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.SchemaReader;
import com.rcggs.datalake.core.model.TransformerConfig;
import com.rcggs.datalake.security.encryption.EncryptDecryptUtil;

@Singleton
public class DatalakeConfiguration {

	final Logger logger = Logger.getLogger(getClass());

	private ConcurrentMap<String, ConnectionConfig> services;
	private ConcurrentMap<String, ConnectionConfig> connections;
	private ConcurrentMap<String, TransformerConfig> transformations;
	private ConcurrentMap<String, String> properties;
	private ConcurrentMap<String, SchemaReader> schemaReaders;
	private ConcurrentMap<String, String> drivers;
	public static String DATALAKE_CONF = "datalake.conf";
	public static String DATALAKE_SECRET_KEY = "datalake.key";

	public DatalakeConfiguration() {
		this.configure();
	}

	void configure() {

		try {
			this.services = new ConcurrentHashMap<String, ConnectionConfig>();
			this.connections = new ConcurrentHashMap<String, ConnectionConfig>();
			this.transformations = new ConcurrentHashMap<String, TransformerConfig>();
			this.schemaReaders = new ConcurrentHashMap<String, SchemaReader>();
			this.properties = new ConcurrentHashMap<String, String>();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			Document doc = null;

			if (System.getProperty(DATALAKE_CONF) != null) {
				logger.info("opening config file from " + System.getProperty(DATALAKE_CONF));
				doc = db.parse(new File(System.getProperty(DATALAKE_CONF)));
			} else
				doc = db.parse(getClass().getResourceAsStream("/enable-site.xml"));

			doc.getDocumentElement().normalize();

			NodeList properties = doc.getElementsByTagName("property");
			for (int i = 0; i < properties.getLength(); ++i) {
				String name = (((Element) properties.item(i)).getElementsByTagName("name").item(0).getTextContent()
						.trim());
				String value = (((Element) properties.item(i)).getElementsByTagName("value").item(0).getTextContent()
						.trim());
				this.properties.put(name, value);
			}

			NodeList drivers = doc.getElementsByTagName("driver");
			for (int i = 0; i < drivers.getLength(); ++i) {
				String clazz = (((Element) drivers.item(i)).getElementsByTagName("class").item(0).getTextContent());
				DriverManager.registerDriver((Driver) Class.forName(clazz).newInstance());
			}

			NodeList transformations = doc.getElementsByTagName("transformation");
			for (int i = 0; i < transformations.getLength(); ++i) {
				TransformerConfig transformation = new TransformerConfig();
				String name = (((Element) transformations.item(i)).getElementsByTagName("name").item(0).getTextContent()
						.trim());
				transformation.setName(name);
				String type = (((Element) transformations.item(i)).getElementsByTagName("type").item(0).getTextContent()
						.trim());
				transformation.setType(type);
				String className = (((Element) transformations.item(i)).getElementsByTagName("class").item(0)
						.getTextContent().trim());
				transformation.setClassName(className);

				this.transformations.put(name != null ? name : "", transformation);

			}

			NodeList schemaReaders = doc.getElementsByTagName("schema-reader");
			for (int i = 0; i < schemaReaders.getLength(); ++i) {
				SchemaReader reader = new SchemaReader();
				String name = (((Element) schemaReaders.item(i)).getElementsByTagName("name").item(0).getTextContent()
						.trim());
				String value = (((Element) schemaReaders.item(i)).getElementsByTagName("type").item(0).getTextContent()
						.trim());

				NodeList paths = ((Element) schemaReaders.item(i)).getElementsByTagName("schema-path");
				Map<String, String> pathMap = new HashMap<>();
				for (int j = 0; j < paths.getLength(); ++j) {
					String type = ((Element) paths.item(j)).getElementsByTagName("type").item(0).getTextContent();
					String path = ((Element) paths.item(j)).getElementsByTagName("path").item(0).getTextContent();
					pathMap.put(type, path);
				}

				reader.setName(name);
				reader.setValue(value);
				reader.setPathMap(pathMap);

				this.properties.put(name, value);
				this.schemaReaders.put(name != null ? name : "", reader);
			}

			NodeList connections = doc.getElementsByTagName("connection");

			logger.info("# of conections is : " + connections.getLength());

			for (int i = 0; i < connections.getLength(); ++i) {
				ConnectionConfig connection = new ConnectionConfig();
				String name = (((Element) connections.item(i)).getElementsByTagName("name").item(0).getTextContent()
						.trim());
				connection.setName(name != null ? name : "");

				String user = (((Element) connections.item(i)).getElementsByTagName("user").item(0).getTextContent()
						.trim());
				connection.setUser(user != null ? user : "");
				String pwd = (((Element) connections.item(i)).getElementsByTagName("pwd").item(0).getTextContent()
						.trim());
				try {

					String secretKey = System.getProperty(DATALAKE_SECRET_KEY);
					if (secretKey == null || "".equals(secretKey)) {
						logger.warn("unable to load secret key -Ddatalake.key arguement not set.");
						InputStream keyStream = getClass().getResourceAsStream("/datalake.key");
						connection.setPwd(pwd != null ? EncryptDecryptUtil.decrypt(keyStream, pwd) : "");
					} else {
						connection.setPwd(pwd != null ? EncryptDecryptUtil.decrypt(secretKey, pwd) : "");
					}

				} catch (Exception e) {

					e.printStackTrace();
					logger.warn(e.getMessage());
					connection.setPwd(pwd != null ? pwd : "");
				}

				logger.info("connection name is " + name);
				String type = (((Element) connections.item(i)).getElementsByTagName("type").item(0).getTextContent()
						.trim());
				connection.setType(type != null ? type : "");
				String port = (((Element) connections.item(i)).getElementsByTagName("port").item(0).getTextContent()
						.trim());
				connection.setPort(port != null ? port : "");

				String host = (((Element) connections.item(i)).getElementsByTagName("host").item(0).getTextContent()
						.trim());
				connection.setHost(host != null ? host : "");

				String path = (((Element) connections.item(i)).getElementsByTagName("path").item(0).getTextContent()
						.trim());
				connection.setPath(path != null ? path : "");

				String clazz = (((Element) connections.item(i)).getElementsByTagName("class").item(0).getTextContent()
						.trim());
				connection.setClazz(clazz != null ? clazz : "");

				NodeList extra = (((Element) connections.item(i)).getElementsByTagName("prop"));
				if (extra.getLength() > 0) {
					Properties props = new Properties();
					for (int j = 0; j < extra.getLength(); ++j) {
						String propName = (((Element) extra.item(j)).getElementsByTagName("name").item(0)
								.getTextContent().trim());
						String propValue = (((Element) extra.item(j)).getElementsByTagName("value").item(0)
								.getTextContent().trim());
						props.put(propName, propValue);
					}
					connection.setProperties(props);
				}

				this.connections.put(name, connection);
			}

			NodeList services = doc.getElementsByTagName("service");

			for (int i = 0; i < services.getLength(); ++i) {
				ConnectionConfig connection = new ConnectionConfig();
				String name = (((Element) services.item(i)).getElementsByTagName("name").item(0).getTextContent()
						.trim());
				connection.setName(name != null ? name : "");

				String user = (((Element) services.item(i)).getElementsByTagName("user").item(0).getTextContent()
						.trim());
				connection.setUser(user != null ? user : "");

				NodeList extra = (((Element) services.item(i)).getElementsByTagName("prop"));
				if (extra.getLength() > 0) {
					Properties props = new Properties();
					for (int j = 0; j < extra.getLength(); ++j) {
						String propName = (((Element) extra.item(j)).getElementsByTagName("name").item(0)
								.getTextContent().trim());
						String propValue = (((Element) extra.item(j)).getElementsByTagName("value").item(0)
								.getTextContent().trim());
						props.put(propName, propValue);
					}
					connection.setProperties(props);
				}

				this.services.put(name, connection);
			}
		} catch (Exception e) {
			logger.info("error occured during configuratio  " + e.getMessage());
			e.printStackTrace();
		}
	}

	public ConcurrentMap<String, SchemaReader> getSchemaReaders() {
		return schemaReaders;
	}

	public void setSchemaReaders(ConcurrentMap<String, SchemaReader> schemaReaders) {
		this.schemaReaders = schemaReaders;
	}

	public ConcurrentMap<String, ConnectionConfig> getConnections() {
		return connections;
	}

	public void setConnections(ConcurrentMap<String, ConnectionConfig> connections) {
		this.connections = connections;
	}

	public ConcurrentMap<String, TransformerConfig> getTransformations() {
		return transformations;
	}

	public void setTransformations(ConcurrentMap<String, TransformerConfig> transformations) {
		this.transformations = transformations;
	}

	public ConcurrentMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(ConcurrentMap<String, String> properties) {
		this.properties = properties;
	}

	public ConcurrentMap<String, String> getDrivers() {
		return drivers;
	}

	public void setDrivers(ConcurrentMap<String, String> drivers) {
		this.drivers = drivers;
	}

	public ConcurrentMap<String, ConnectionConfig> getServices() {
		return services;
	}

	public void setServices(ConcurrentMap<String, ConnectionConfig> services) {
		this.services = services;
	}

	public static void main(String[] args) {
		DatalakeConfiguration dlc = new DatalakeConfiguration();
		System.err.println(dlc.getServices().get("sms").getProperties());
	}
}
