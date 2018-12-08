package com.rcggs.datalake.core.model;

import java.util.List;
import java.util.Map;

public class Level {

	private String id;
	private String text;
	private List<Level> children;
	private String opened;
	private String parent;
	private String type;
	private String itemType;
	private String iconCls;
	private String name;
	Map<String, Object> data;
	List<SchemaDef> schema;
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SchemaDef> getSchema() {
		return schema;
	}

	public void setSchema(List<SchemaDef> schema) {
		this.schema = schema;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getOpened() {
		return opened;
	}

	public void setOpened(String opened) {
		this.opened = opened;
	}

	public List<Level> getChildren() {
		return children;
	}

	public void setChildren(List<Level> children) {
		this.children = children;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}
}
