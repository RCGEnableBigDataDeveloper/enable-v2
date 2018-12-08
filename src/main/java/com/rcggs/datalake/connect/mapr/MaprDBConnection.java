package com.rcggs.datalake.connect.mapr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class MaprDBConnection extends AbstractDataLakeConnection implements DataLakeConnection {


	private ConnectionConfig config;

	public MaprDBConnection(ConnectionConfig config) {
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
				.resource("http://" + config.getHost() + ":8080/sample/hello/maprdb/uuu");
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

		// final Map<ItemDefinition, List<ItemDefinition>> map = new
		// HashMap<ItemDefinition, List<ItemDefinition>>();
		// final List<ItemDefinition> columnDefs = new
		// ArrayList<ItemDefinition>();
		// final Map<String, Object> dataMap = new HashMap<String, Object>();
		// dataMap.put("config", config);
		//
		// final Admin admin = MapRDB.newAdmin();
		// List<Path> tables = admin.listTables(new Path(config.getPath()));
		// logger.info(String.format("found %s tables in path %s",
		// tables.size(), config.getPath()));
		//
		// for (final Path tablePath : admin.listTables(new
		// Path(config.getPath()))) {
		// ItemDefinition cd = new ItemDefinition();
		// cd.setName(tablePath.getName());
		// cd.setItemType("table");
		// cd.setType(config.getType());
		// cd.setData(dataMap);
		// columnDefs.add(cd);
		// }
		//
		// final ItemDefinition idef = new ItemDefinition();
		// idef.setName(config.getHost());
		// idef.setType(config.getType());
		// idef.setData(dataMap);
		// idef.setChildren(columnDefs);
		//
		// map.put(idef, columnDefs);

		return map;
	}
}
