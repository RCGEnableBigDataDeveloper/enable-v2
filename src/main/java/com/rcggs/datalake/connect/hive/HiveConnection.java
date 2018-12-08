package com.rcggs.datalake.connect.hive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcggs.datalake.configuration.DatalakeContext;
import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class HiveConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	public HiveConnection(ConnectionConfig config) {
		super(config);
		this.config = config;
	}

	final Logger logger = LoggerFactory.getLogger(getClass());

	java.sql.Connection connection;
	ConnectionConfig config;

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(String name) throws Exception {

		final Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		Client client = Client.create();
		
		WebResource webResource = client
				.resource("http://" + config.getHost() + ":9987/enable/api/v1/hive/asdafkjlsdlkf");
		ClientResponse res = (ClientResponse) ((com.sun.jersey.api.client.WebResource.Builder) webResource
				.accept(new String[] { "application/json" }).type("application/json")).post(ClientResponse.class,
						config);
		if (res.getStatus() == 500)
			DatalakeContext.getJobDao().updateJob("id", "failed");
		else
			DatalakeContext.getJobDao().updateJob("id", "running");
		String result = res.getEntity(String.class);

		ObjectMapper mapper = new ObjectMapper();

		Map<String, List<ItemDefinition>> items = mapper.readValue(result,
				new TypeReference<Map<String, List<ItemDefinition>>>() {
				});

		String key = items.entrySet().iterator().next().getKey();
		List<ItemDefinition> values = items.entrySet().iterator().next().getValue();
		ItemDefinition idef = new ItemDefinition();
		idef.setName(key);
		idef.setData(data);

		map.put(idef, values);

		return map;

	}
}
