package com.rcggs.datalake.core.model;

import java.io.Serializable;
import java.util.Properties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement  
public class ConnectionConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public ConnectionConfig(){
		
	}
	
	private String name;
	private String host;
	private String path;
	private String user;
	private String pwd;
	private String type;
	private String port;
	private String clazz;

	Properties properties;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
}
