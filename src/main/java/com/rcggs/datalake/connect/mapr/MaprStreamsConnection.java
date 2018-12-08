package com.rcggs.datalake.connect.mapr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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

public class MaprStreamsConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	private final Logger logger = Logger.getLogger(getClass());

	private ConnectionConfig config;

	public MaprStreamsConnection(ConnectionConfig config) {
		super(config);
		this.config = config;
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(final String name) throws Exception {

		final Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		Client client = Client.create();
		WebResource webResource = client
				.resource("http://" + config.getHost() + ":8080/sample/hello/streams/uuu");
		ClientResponse res = webResource.accept("application/json").type("application/json").post(ClientResponse.class,
				config);
		if (res.getStatus() == 500) {
			DatalakeContext.getJobDao().updateJob("id", "failed");
		} else {
			DatalakeContext.getJobDao().updateJob("id", "running");
		}

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
