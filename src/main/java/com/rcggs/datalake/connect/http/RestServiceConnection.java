package com.rcggs.datalake.connect.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class RestServiceConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	ConnectionConfig config;

	public RestServiceConnection(ConnectionConfig config) {
		super(config);
		try {
			this.config = config;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(String name) throws Exception {
		final Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		try {
			List<ItemDefinition> columnDefs = new ArrayList<ItemDefinition>();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("config", config);

			ItemDefinition idef = new ItemDefinition();
			idef.setName(config.getHost());
			idef.setItemType("directory");
			idef.setType(config.getType());
			idef.setData(data);

			Client client = Client.create();
			WebResource webResource = client.resource(config.getPath());
			ClientResponse res = webResource.type("application/json").get(ClientResponse.class);
			if (res.getStatus() == 500) {
			} else {

				String results = res.getEntity(String.class);
				String[] names = results.split(",");

				System.err.println(results);
				for (String filename : names) {
					ItemDefinition cd = new ItemDefinition();
					String _name_ = filename.replaceAll("\\[", "").replaceAll("\"", "").replaceAll("\\]", "");
					cd.setName(_name_.substring(1, _name_.length() - 1));
					cd.setItemType("file");
					cd.setType(config.getType());
					cd.setData(data);
					columnDefs.add(cd);
				}

				idef.setChildren(columnDefs);

				map.put(idef, columnDefs);

			}

			return map;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return null;
	}
}
