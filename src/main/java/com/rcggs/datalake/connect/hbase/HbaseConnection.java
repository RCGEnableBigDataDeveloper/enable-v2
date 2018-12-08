package com.rcggs.datalake.connect.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
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

public class HbaseConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	private final Logger logger = Logger.getLogger(getClass());

	ConnectionConfig config;

	public HbaseConnection(ConnectionConfig config) {
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
				.resource("http://" + config.getHost() + ":8080/sample/hello/hbase/uuu");
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

		// Configuration configuration = HBaseConfiguration.create();
		// final Map<ItemDefinition, List<ItemDefinition>> map = new
		// HashMap<ItemDefinition, List<ItemDefinition>>();
		// final Map<String, Object> data = new HashMap<String, Object>();
		// data.put("config", config);
		//
		// if (config.getProperties() != null &&
		// config.getProperties().getProperty("kerberos.principal") != null) {
		//
		// logger.info("hbase krb user " +
		// config.getProperties().getProperty("kerberos.principal") + ":"
		// + config.getProperties().getProperty("kerberos.keytab"));
		//
		// configuration.set("hadoop.security.authentication", "Kerberos");
		// UserGroupInformation.setConfiguration(configuration);
		// UserGroupInformation ugi = null;
		//
		// ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(
		// config.getProperties().getProperty("kerberos.principal"),
		// config.getProperties().getProperty("kerberos.keytab"));
		// ugi.doAs(new PrivilegedExceptionAction<Void>() {
		// public Void run() throws Exception {
		// describe(map, data);
		// return null;
		// }
		// });
		//
		// } else {
		// describe(map, data);
		// }

		return map;
	}

	void describe(final Map<ItemDefinition, List<ItemDefinition>> map, final Map<String, Object> data) {
		HBaseAdmin admin = null;
		try {

			Configuration configuration = HBaseConfiguration.create();
			admin = new HBaseAdmin(configuration);
			logger.info("successfully created hbase admin " + admin);

			HTableDescriptor[] tableDescriptor = admin.listTables();
			List<ItemDefinition> columnDefs = new ArrayList<ItemDefinition>();

			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("config", config);

			for (int i = 0; i < tableDescriptor.length; i++) {
				ItemDefinition cd = new ItemDefinition();
				cd.setName(tableDescriptor[i].getNameAsString());
				cd.setItemType("table");
				cd.setType(config.getType());
				cd.setData(dataMap);
				columnDefs.add(cd);

			}

			ItemDefinition idef = new ItemDefinition();
			idef.setName(config.getHost());
			idef.setData(dataMap);
			map.put(idef, columnDefs);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				admin.close();
				logger.info("closing hbase admin session");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}