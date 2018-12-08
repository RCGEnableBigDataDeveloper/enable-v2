
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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "routeid",
    "userid",
    "general",
    "source",
    "target"
})
public class Route implements Serializable {

    @JsonProperty("routeid")
    private String routeid;
    @JsonProperty("userid")
    private String userid;
    @JsonProperty("general")
    private List<Object> general = new ArrayList<Object>();
    @JsonProperty("source")
    private Source source;
    @JsonProperty("target")
    private Target target;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The routeid
     */
    @JsonProperty("routeid")
    public String getRouteid() {
        return routeid;
    }

    /**
     * 
     * @param routeid
     *     The routeid
     */
    @JsonProperty("routeid")
    public void setRouteid(String routeid) {
        this.routeid = routeid;
    }

    /**
     * 
     * @return
     *     The userid
     */
    @JsonProperty("userid")
    public String getUserid() {
        return userid;
    }

    /**
     * 
     * @param userid
     *     The userid
     */
    @JsonProperty("userid")
    public void setUserid(String userid) {
        this.userid = userid;
    }    
    
    /**
     * 
     * @return
     *     The general
     */
    @JsonProperty("general")
    public List<Object> getGeneral() {
        return general;
    }

    /**
     * 
     * @param general
     *     The general
     */
    @JsonProperty("general")
    public void setGeneral(List<Object> general) {
        this.general = general;
    }

    /**
     * 
     * @return
     *     The source
     */
    @JsonProperty("source")
    public Source getSource() {
        return source;
    }

    /**
     * 
     * @param source
     *     The source
     */
    @JsonProperty("source")
    public void setSource(Source source) {
        this.source = source;
    }

    /**
     * 
     * @return
     *     The target
     */
    @JsonProperty("target")
    public Target getTarget() {
        return target;
    }

    /**
     * 
     * @param target
     *     The target
     */
    @JsonProperty("target")
    public void setTarget(Target target) {
        this.target = target;
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
        return new HashCodeBuilder().append(routeid).append(general).append(source).append(target).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Route) == false) {
            return false;
        }
        Route rhs = ((Route) other);
        return new EqualsBuilder().append(routeid, rhs.routeid).append(general, rhs.general).append(source, rhs.source).append(target, rhs.target).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
