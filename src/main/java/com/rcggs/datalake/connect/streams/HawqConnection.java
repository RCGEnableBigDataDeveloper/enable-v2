package com.rcggs.datalake.connect.streams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;

public class HawqConnection extends AbstractDataLakeConnection implements
		DataLakeConnection {

	final Logger LOG = LoggerFactory.getLogger(getClass());

	ConnectionConfig config;

	public HawqConnection(ConnectionConfig config) {
		super(config);
		try {
			this.config = config;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(final String name)
			throws Exception {
		Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();

		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, String> schema = new HashMap<String, String>();
//		schema.put("userid", "json string");
//		schema.put("displayname", "json string");
//		schema.put("inreplyto", "json string");
//		schema.put("date", "json string");
//		schema.put("latlong", "json string");
//		schema.put("tweet", "json string");

		data.put("config", config);
		try {
			List<ItemDefinition> columnDefs = new ArrayList<ItemDefinition>();

			ItemDefinition cd = new ItemDefinition();
			cd.setName("db0 - <font color=red>offline</font>");
			cd.setData(data);
			cd.setLeaf(true);
		//	cd.setSchema(schema);

			map.put(cd, columnDefs);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

}