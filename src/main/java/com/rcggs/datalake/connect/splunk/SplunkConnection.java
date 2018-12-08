package com.rcggs.datalake.connect.splunk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;
import com.splunk.EntityCollection;
import com.splunk.HttpService;
import com.splunk.Index;
import com.splunk.SSLSecurityProtocol;
import com.splunk.Service;
import com.splunk.ServiceArgs;

public class SplunkConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	ConnectionConfig config;

	public SplunkConnection(ConnectionConfig config) {
		super(config);
		try {
			this.config = config;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(final String name) throws Exception {
		Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		HttpService.setSslSecurityProtocol(SSLSecurityProtocol.TLSv1_2);

		ServiceArgs loginArgs = new ServiceArgs();
		loginArgs.setUsername(config.getUser());
		loginArgs.setPassword(config.getPwd());
		loginArgs.setHost(config.getHost());
		loginArgs.setPort(8099);
		Service service = Service.connect(loginArgs);

		// for (Application app : service.getApplications().values()) {
		// logger.info(app.getName());
		// }

		EntityCollection<Index> indexes = service.getIndexes();
		List<ItemDefinition> indexDefs = new ArrayList<ItemDefinition>();
		for (String idx : indexes.keySet()) {
			if (!idx.startsWith("_")) {

				ItemDefinition cd = new ItemDefinition();
				cd.setName(idx);
				cd.setData(data);
				cd.setItemType("index");
				indexDefs.add(cd);

			}

		}

		ItemDefinition idef = new ItemDefinition();
		idef.setName("indicies");
		idef.setItemType("splunk");

		map.put(idef, indexDefs);

		return map;

	}
}