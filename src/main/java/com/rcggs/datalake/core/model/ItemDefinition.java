package com.rcggs.datalake.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ItemDefinition implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
	private String itemType;
	private String length;
	private String precision;
	private String scale;
	private String parent;
	private Map<String, Object> data;
	private List<SchemaDef> schema;
	private List<ItemDefinition> children;
	private boolean leaf;

	public boolean isLeaf() {
		return leaf;
	}

	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

	public List<SchemaDef> getSchema() {
		return schema;
	}

	public void setSchema(List<SchemaDef> schema) {
		this.schema = schema;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public List<ItemDefinition> getChildren() {
		return children;
	}

	public void setChildren(List<ItemDefinition> children) {
		this.children = children;
	}
}
