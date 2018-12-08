package com.rcggs.datalake.core.model;

import java.io.Serializable;
import java.util.List;

public class SchemaDef implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String value;
	private boolean editing;
	private String udfname;
	private String type;
	private boolean checked;
	private String delimiter;
	private String format;
	private String defaultValue;
	private String start;
	private String end;
	private String parent;
	private String group;
	private List<String>  restrictions;
	private String namespace;
	private String urn;
	private int width;
	private boolean isFixedWidth;

	
	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}

	private int startPos;
	private int endPos;
	
	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public boolean isFixedWidth() {
		return isFixedWidth;
	}

	public void setFixedWidth(boolean isFixedWidth) {
		this.isFixedWidth = isFixedWidth;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public String getUdfname() {
		return udfname;
	}

	public void setUdfname(String udfname) {
		this.udfname = udfname;
	}

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

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public List<String> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(List<String> restrictions) {
		this.restrictions = restrictions;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getUrn() {
		return urn;
	}

	public void setUrn(String urn) {
		this.urn = urn;
	}
}

