package com.rcggs.datalake.connect.mapr;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.opcfoundation.ua.builtintypes.DataValue;
import org.opcfoundation.ua.builtintypes.DateTime;
import org.opcfoundation.ua.builtintypes.LocalizedText;
import org.opcfoundation.ua.builtintypes.NodeId;
import org.opcfoundation.ua.core.ApplicationDescription;
import org.opcfoundation.ua.core.ApplicationType;
import org.opcfoundation.ua.core.Identifiers;
import org.opcfoundation.ua.core.TimestampsToReturn;
import org.opcfoundation.ua.transport.security.SecurityMode;

import com.prosysopc.ua.ApplicationIdentity;
import com.prosysopc.ua.SecureIdentityException;
import com.prosysopc.ua.client.UaClient;

public class ProSysTest {
	
	public static void main(String[] args) {
		try {
			getNodeData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Map<String, String> getNodeData() throws Exception {
		UaClient client = new UaClient("opc.tcp://C439:53530/OPCUA/SimulationServer");
		client.setSecurityMode(SecurityMode.NONE);
		initialize(client);
		client.connect();
		DataValue value = client.readValue(Identifiers.Server_ServerStatus_State);
		System.err.println(value);

		Map<String, String> values = new HashMap<String, String>();
		String[] nodes = new String[] { "Counter1", "Random1", "Sinusoid1", "Square1", "Sawtooth1", "ppp", "Triangle1",
				"Sawtooth2", "Expression1" };

		for (String node : nodes) {
			NodeId nodeId = new NodeId(5, node);
			DataValue[] result = client.historyReadRaw(nodeId, DateTime.MIN_VALUE, DateTime.currentTime(), null, true,
					null, TimestampsToReturn.Source);

			System.err.println(result[0].getValue());
			values.put(node, result[0].getValue().toString());
		}

		client.disconnect();

		return values;

		// EndpointDescription[] endpoints = client.discoverEndpoints();
		// for(int i = 0; i < endpoints.length; i++) {
		//
		// System.err.println(endpoints[i].getEndpointUrl());
		// }

	}

	protected static void initialize(UaClient client)
			throws SecureIdentityException, IOException, UnknownHostException {
		// *** Application Description is sent to the server
		ApplicationDescription appDescription = new ApplicationDescription();
		appDescription.setApplicationName(new LocalizedText("SimpleClient", Locale.ENGLISH));
		// 'localhost' (all lower case) in the URI is converted to the actual
		// host name of the computer in which the application is run
		appDescription.setApplicationUri("urn:localhost:UA:SimpleClient");
		appDescription.setProductUri("urn:prosysopc.com:UA:SimpleClient");
		appDescription.setApplicationType(ApplicationType.Client);

		final ApplicationIdentity identity = new ApplicationIdentity();
		identity.setApplicationDescription(appDescription);
		client.setApplicationIdentity(identity);
	}

}
