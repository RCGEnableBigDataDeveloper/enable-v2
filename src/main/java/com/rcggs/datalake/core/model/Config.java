
package com.rcggs.datalake.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "name",
    "host",
    "path",
    "user",
    "pwd",
    "type",
    "port",
    "clazz",
    "properties"
})
public class Config implements Serializable{

    @JsonProperty("name")
    private String name;
    @JsonProperty("host")
    private String host;
    @JsonProperty("path")
    private String path;
    @JsonProperty("user")
    private String user;
    @JsonProperty("pwd")
    private String pwd;
    @JsonProperty("type")
    private String type;
    @JsonProperty("port")
    private String port;
    @JsonProperty("clazz")
    private String clazz;
    @JsonProperty("properties")
    private Object properties;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The host
     */
    @JsonProperty("host")
    public String getHost() {
        return host;
    }

    /**
     * 
     * @param host
     *     The host
     */
    @JsonProperty("host")
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * 
     * @return
     *     The path
     */
    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    /**
     * 
     * @param path
     *     The path
     */
    @JsonProperty("path")
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 
     * @return
     *     The user
     */
    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    /**
     * 
     * @param user
     *     The user
     */
    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * 
     * @return
     *     The pwd
     */
    @JsonProperty("pwd")
    public String getPwd() {
        return pwd;
    }

    /**
     * 
     * @param pwd
     *     The pwd
     */
    @JsonProperty("pwd")
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * 
     * @return
     *     The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The port
     */
    @JsonProperty("port")
    public String getPort() {
        return port;
    }

    /**
     * 
     * @param port
     *     The port
     */
    @JsonProperty("port")
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * 
     * @return
     *     The clazz
     */
    @JsonProperty("clazz")
    public String getClazz() {
        return clazz;
    }

    /**
     * 
     * @param clazz
     *     The clazz
     */
    @JsonProperty("clazz")
    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    /**
     * 
     * @return
     *     The properties
     */
    @JsonProperty("properties")
    public Object getProperties() {
        return properties;
    }

    /**
     * 
     * @param properties
     *     The properties
     */
    @JsonProperty("properties")
    public void setProperties(Object properties) {
        this.properties = properties;
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
        return new HashCodeBuilder().append(name).append(host).append(path).append(user).append(pwd).append(type).append(port).append(clazz).append(properties).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Config) == false) {
            return false;
        }
        Config rhs = ((Config) other);
        return new EqualsBuilder().append(name, rhs.name).append(host, rhs.host).append(path, rhs.path).append(user, rhs.user).append(pwd, rhs.pwd).append(type, rhs.type).append(port, rhs.port).append(clazz, rhs.clazz).append(properties, rhs.properties).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
