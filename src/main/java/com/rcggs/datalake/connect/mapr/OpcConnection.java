package com.rcggs.datalake.connect.mapr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.opcfoundation.ua.application.Client;
import org.opcfoundation.ua.transport.Endpoint;
import org.opcfoundation.ua.transport.SecureChannel;
import org.opcfoundation.ua.transport.security.Cert;
import org.opcfoundation.ua.transport.security.KeyPair;
import org.opcfoundation.ua.transport.security.PrivKey;

import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;
import com.rcggs.datalake.core.model.SchemaDef;

public class OpcConnection extends AbstractDataLakeConnection implements DataLakeConnection

{

	String uri;
	// Server server;
	Client client;
	Endpoint endpoint;
	SecureChannel secureChannel;
	SecureChannel unsecureChannel;

	private final Logger logger = Logger.getLogger(getClass());

	private ConnectionConfig config;

	public OpcConnection(ConnectionConfig config) {
		super(config);
		this.config = config;
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(final String name) throws Exception {

		final Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		// server = Server.createServerApplication();
		Cert myServerCertificate = Cert.load(EndpointsTest.class.getResource("/ServerCert.der"));
		PrivKey myServerPrivateKey = PrivKey.loadFromKeyStore(EndpointsTest.class.getResource("/UAServerCert.pfx"),
				"Opc.Sample.Ua.Server");
		KeyPair serverApplicationInstanceCertificate = new KeyPair(myServerCertificate, myServerPrivateKey);
		// server.getApplication().addApplicationInstanceCertificate(serverApplicationInstanceCertificate);

		Set<String> endpoints = EndpointsTest.getEndpoints(new String[0]);
		endpoints.add("https://eperler_lt:53443/OPCUA/SimulationServer");
		List<SchemaDef> schema = new LinkedList<>();
		String[] nodes = new String[] { "Counter1", "Random1", "Sinusoid1", "Square1", "Sawtooth1", "Triangle1",
				"Expression1" };
		for (String node : nodes) {
			SchemaDef sd = new SchemaDef();
			sd.setName(node);
			sd.setValue("string");
			schema.add(sd);
		}

		final List<ItemDefinition> columnDefs = new ArrayList<ItemDefinition>();
		ItemDefinition cd = new ItemDefinition();
		cd.setName("endpoints");
		cd.setItemType("table");
		cd.setType(config.getType());
		cd.setData(data);

		for (Iterator<String> it = endpoints.iterator(); it.hasNext();) {
			String ed = it.next();
			ItemDefinition cd1 = new ItemDefinition();
			cd1.setName(ed);
			cd1.setItemType("table");
			cd1.setType(config.getType());
			cd1.setData(data);
			cd1.setSchema(schema);

			columnDefs.add(cd1);
		}

		map.put(cd, columnDefs);

		return map;
	}

}
