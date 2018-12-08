package com.rcggs.datalake.core.model;

import java.io.Serializable;
import java.util.List;

public class Resources implements Serializable{

	private String text;
	private List<SchemaDef> schema;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<SchemaDef> getSchema() {
		return schema;
	}

	public void setSchema(List<SchemaDef> schema) {
		this.schema = schema;
	}
}
