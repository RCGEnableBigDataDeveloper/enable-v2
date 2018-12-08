package com.rcggs.datalake.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "id", "nodeid", "itemtype", "name", "type", "parent", "options", "advanced", "config", "resources" })
public class Target implements Endpoint, Serializable {

	@JsonProperty("id")
	private String id;
	@JsonProperty("nodeid")
	private String nodeid;
	@JsonProperty("name")
	private String name;
	@JsonProperty("type")
	private String type;
	@JsonProperty("itemtype")
	private String itemtype;
	@JsonProperty("filter")
	private String filter;
	@JsonProperty("parent")
	private String parent;
	@JsonProperty("options")
	private List<Object> options = new ArrayList<Object>();
	@JsonProperty("advanced")
	private List<Object> advanced = new ArrayList<Object>();
	@JsonProperty("config")
	private Config config;
	
	@JsonProperty("schema")
	private List<SchemaDef> schema;

	@JsonProperty("resources")
	private List<Resources> resources = new ArrayList<Resources>();
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The id
	 */
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 *            The id
	 */
	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}
	

	public List<SchemaDef> getSchema() {
		return schema;
	}

	public void setSchema(List<SchemaDef> schema) {
		this.schema = schema;
	}


	/**
	 * 
	 * @return The nodeid
	 */
	@JsonProperty("nodeid")
	public String getNodeid() {
		return nodeid;
	}

	/**
	 * 
	 * @param nodeid
	 *            The nodeid
	 */
	@JsonProperty("nodeid")
	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	/**
	 * 
	 * @return The name
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            The name
	 */
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return The type
	 */
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 *            The type
	 */
	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return The parent
	 */
	@JsonProperty("parent")
	public String getParent() {
		return parent;
	}

	/**
	 * 
	 * @param parent
	 *            The parent
	 */
	@JsonProperty("parent")
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * 
	 * @return The options
	 */
	@JsonProperty("options")
	public List<Object> getOptions() {
		return options;
	}

	/**
	 * 
	 * @param options
	 *            The options
	 */
	@JsonProperty("options")
	public void setOptions(List<Object> options) {
		this.options = options;
	}

	/**
	 * 
	 * @return The advanced
	 */
	@JsonProperty("advanced")
	public List<Object> getAdvanced() {
		return advanced;
	}

	/**
	 * 
	 * @param advanced
	 *            The advanced
	 */
	@JsonProperty("advanced")
	public void setAdvanced(List<Object> advanced) {
		this.advanced = advanced;
	}

	/**
	 * 
	 * @return The config
	 */
	@JsonProperty("config")
	public Config getConfig() {
		return config;
	}

	/**
	 * 
	 * @param config
	 *            The config
	 */
	@JsonProperty("config")
	public void setConfig(Config config) {
		this.config = config;
	}

	@JsonProperty("itemtype")
	public void setItemtype(String itemtype) {
		this.itemtype = itemtype;
	}

	@JsonProperty("itemtype")
	public String getItemtype() {
		return itemtype;
	}

	/**
	 * 
	 * @return The resources
	 */
	@JsonProperty("resources")
	public List<Resources> getResources() {
		return resources;
	}

	/**
	 * 
	 * @param resources
	 *            The resources
	 */
	@JsonProperty("resources")
	public void setResources(List<Resources> resources) {
		this.resources = resources;
	}
	
	

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(nodeid).append(name).append(type).append(parent).append(options).append(advanced)
				.append(config).append(additionalProperties).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof Target) == false) {
			return false;
		}
		Target rhs = ((Target) other);
		return new EqualsBuilder().append(id, rhs.id).append(nodeid, rhs.nodeid).append(name, rhs.name).append(type, rhs.type)
				.append(parent, rhs.parent).append(options, rhs.options).append(advanced, rhs.advanced).append(config, rhs.config)
				.append(additionalProperties, rhs.additionalProperties).isEquals();
	}

}
