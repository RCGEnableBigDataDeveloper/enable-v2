package com.rcggs.datalake.core.model;

import java.util.List;
import java.util.Map;

public interface Endpoint {

	public String getId();

	public void setId(String id);

	public String getNodeid();

	public void setNodeid(String nodeid);

	public String getName();

	public void setName(String name);

	public String getType();

	public void setType(String type);
	
	public String getItemtype();

	public void setItemtype(String itemtype);

	public String getParent();

	public void setParent(String parent);

	public List<Object> getOptions();

	public void setOptions(List<Object> options);

	public List<Object> getAdvanced();

	public void setAdvanced(List<Object> advanced);

	public Config getConfig();

	public void setConfig(Config config);

	public Map<String, Object> getAdditionalProperties();

	public void setAdditionalProperty(String name, Object value);

	public List<Resources> getResources();

	public void setResources(List<Resources> resources);
}
