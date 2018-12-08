package com.rcggs.datalake.core.model;

import java.util.Map;

public class SchemaReader {

	private String name;
	private String value;
	private Map<String, String> pathMap;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Map<String, String> getPathMap() {
		return pathMap;
	}

	public void setPathMap(Map<String, String> pathMap) {
		this.pathMap = pathMap;
	}
}
