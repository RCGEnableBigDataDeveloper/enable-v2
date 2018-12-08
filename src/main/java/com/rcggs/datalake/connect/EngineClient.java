package com.rcggs.datalake.connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import com.rcggs.datalake.configuration.DatalakeContext;
import com.rcggs.engine.zookeeper.EngineZKClient;
import com.rcggs.engine.zookeeper.EngineZKClient.TaskObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class EngineClient implements Job {

	final Logger logger = Logger.getLogger(getClass());
	final static ObjectMapper mapper = new ObjectMapper();
	final static ObjectWriter ow = new ObjectMapper().writer();

	// zookeeper client
	EngineZKClient engineZKClient;
	private String zkhost = null;

	public EngineClient() {

		try {
			zkhost = DatalakeContext.getProperty("zookeeper.host");
			if (zkhost != null) {
				engineZKClient = new EngineZKClient(zkhost);
				engineZKClient.startZK();
			}
		} catch (Exception e) {
			logger.error("Unabale to start Zookeeper client", e);
			throw new RuntimeException("Unable to start EngineClient as zookeeper client", e);
		}
	}

	public void execute(final JobExecutionContext context) {

		String data = String.valueOf(context.getJobDetail().getJobDataMap().get("data"));
		String id = String.valueOf(context.getJobDetail().getJobDataMap().get("id"));

		ObjectMapper mapper = new ObjectMapper();
		try {

			String path = "";

			org.codehaus.jackson.JsonNode route = mapper.readTree(data);

			// if
			// (route.get(0).get("source").get("config").get("type").getTextValue().equals("opc"))
			// {
			//
			// for (int i = 0; i < 100; i++) {
			// Map<String, String> map = ProSysTest.getNodeData();
			// map.put("id", String.valueOf(i));
			//
			// Client client = Client.create();
			// WebResource webResource =
			// client.resource("http://127.0.0.1:8080/sample/hello/run/uuu");
			// ClientResponse res =
			// webResource.accept("application/json").type("application/json")
			// .post(ClientResponse.class, map);
			// if (res.getStatus() == 500) {
			// DatalakeContext.getJobDao().updateJob("id", "failed");
			// } else {
			// DatalakeContext.getJobDao().updateJob("id", "running");
			// }
			//
			// System.err.println(map);
			// // String result = res.getEntity(String.class);
			// Thread.sleep(1000);
			//
			// }
			// return;
			// }

			for (Iterator<JsonNode> it = route.getElements(); it.hasNext();) {
				JsonNode _route_ = it.next();
				System.out.println(_route_);

				JsonNode target = _route_.get("target");
				JsonNode source = _route_.get("source");

				String key = target.get("name").getTextValue() + "/" + path + "/" + source.get("name").getTextValue()
						+ "|" + target.get("config").get("host").getTextValue() + "|"
						+ target.get("config").get("type").getTextValue();
				key = key.toLowerCase().replaceAll("/+", "/");
				logger.info("generating key for schema " + key);
				DatalakeContext.getJobDao().saveMetadata(key,
						source.get("schema") != null ? source.get("schema").toString() : "",
						source.get("config").toString(),
						target.get("config").toString(),
						route.get(0).get("userid").getTextValue().trim(), System.currentTimeMillis());

			}

			// if (source.get("schema") != null) {
			// String key = target.get("name").getTextValue() + "/" + path + "/"
			// + source.get("name").getTextValue()
			// + "|" + target.get("config").get("host").getTextValue() + "|"
			// + target.get("config").get("type").getTextValue();
			// key = key.toLowerCase().replaceAll("/+", "/");
			// logger.info("generating key for schema " + key);
			// DatalakeContext.getJobDao().saveMetadata(key,
			// source.get("schema").toString(),
			// source.get("config").get("host").getTextValue(),
			// target.get("config").get("host").getTextValue(),
			// route.get(0).get("userid").getTextValue().trim(),
			// System.currentTimeMillis());
			// }

			String serviceName = null;
			String serviceTags = null;
			String serviceDescription = null;
			for (Iterator<JsonNode> iter = route.get(0).get("target").get("options").iterator(); iter.hasNext();) {
				JsonNode node = iter.next();

				if (node.get("name").getTextValue().equals("service.name")) {
					serviceName = node.get("value").getTextValue();
				} else if (node.get("name").getTextValue().equals("service.tags")) {
					serviceTags = node.get("value").getTextValue();
				} else if (node.get("name").getTextValue().equals("service.description")) {
					serviceDescription = node.get("value").getTextValue();
				}
			}

			if (serviceName != null) {

				logger.info("request to create service: " + serviceName);
				Connection conn = null;
				PreparedStatement stmt = null;
				Statement st = null;

				try {
					Class.forName(DatalakeContext.getProperty("datalake.metadata.database.driver")).newInstance();
					conn = DriverManager.getConnection(DatalakeContext.getProperty("datalake.metadata.database.url"),
							DatalakeContext.getProperty("datalake.metadata.database.user"),
							DatalakeContext.getProperty("datalake.metadata.database.password"));
					st = conn.createStatement();
					ResultSet rs = st.executeQuery("select * from dl_service where name='" + serviceName + "'");
					if (rs.next()) {
						logger.info("service exists.. ignoring");
					} else {
						stmt = conn.prepareStatement(
								"insert into dl_service(id,name,description,tags,route)values(?,?,?,?,?)");
						stmt.setString(1, route.get(0).get("routeid").getTextValue());
						stmt.setString(2, serviceName);
						stmt.setString(3, serviceDescription);
						stmt.setString(4, serviceTags);
						stmt.setString(5, mapper.writeValueAsString(route.get(0)));
						stmt.executeUpdate();
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (st != null)
							st.close();
						if (stmt != null)
							stmt.close();
						if (conn != null)
							conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

			id = String.valueOf(context.getJobDetail().getJobDataMap().get("id"));

			DatalakeContext.getWorkDao().saveWork(id, data);

			// if zookeeper host is provided, then be aware of it
			if (zkhost != null) {
				TaskObject task = new TaskObject();
				engineZKClient.submitTask(id, task);
				DatalakeContext.getJobDao().updateJob(id, "running");
				// call(id, output, false);
			} else {

				// rest client
				Client client = Client.create();
				WebResource webResource = client.resource(DatalakeContext.getProperty("datalake.engine.url"));
				System.out.println(route.toString());
				ClientResponse res = webResource.type("application/json").post(ClientResponse.class, route.toString());
				if (res.getStatus() == 500) {
					DatalakeContext.getJobDao().updateJob(id, "failed");
					// call(id, output, true);
				} else {
					DatalakeContext.getJobDao().updateJob(id, "running");
					// call(id, output, false);
				}
			}
		} catch (Exception e) {
			DatalakeContext.getJobDao().updateJob(id, "failed");
			// call(id, Throwables.getStackTraceAsString(e), true);
			e.printStackTrace();
		}
	}
}