package com.rcggs.enable.data.controller;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rcggs.datalake.configuration.DatalakeContext;
import com.rcggs.datalake.connect.DatalakeConnectionFactory;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.Level;
import com.rcggs.datalake.core.model.SchemaDef;
import com.rcggs.datalake.core.model.SchemaReader;
import com.rcggs.datalake.pool.DataSource;
import com.rcggs.enable.data.resource.ResourceReader;

@RestController
public class MetadataController extends AbstractController {

	final Logger logger = Logger.getLogger(getClass());
	ObjectMapper mapper = new ObjectMapper();

	// @Resource
	DatalakeConnectionFactory datalakeConnectionFactory = new DatalakeConnectionFactory();

	@RequestMapping(value = "/getOptions/{name}", method = RequestMethod.GET, produces = "application/json")
	public String getOptions(final @PathVariable String name) {
		List<Map<String, String>> data = DatalakeContext.getMetadataDao().getOptions(name);
		try {
			return ow.writeValueAsString(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/getMetadata", method = RequestMethod.GET, produces = "application/json")
	public String getMetadata() {
		List<Map<String, String>> response = new LinkedList<Map<String, String>>();
		try {
			Connection con = DataSource.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from dl_metadata");
			while (rs.next()) {
				Map<String, String> map = new HashMap<>();

				map.put("name", rs.getString(2));
				map.put("data", rs.getString(3).replaceAll("\\*", "").replaceAll("\\\\", ""));
				map.put("origin", rs.getString(4));
				map.put("dest", rs.getString(5));
				map.put("usr", rs.getString(6));
				map.put("time", rs.getString(7));
				map.put("props", rs.getString(8));
				
				response.add(map);
			}

			return ow.writeValueAsString(response);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@RequestMapping(value = "/getConnectionsResponse", method = RequestMethod.GET, produces = "application/json")
	public String getConnectionsResponse(@RequestBody String data) {
		return getConnections("");
	}

	@RequestMapping(value = "/getServices", method = RequestMethod.GET, produces = "application/json")
	public String getServices(@RequestBody String data) {
		List<Map<String, String>> response = new LinkedList<Map<String, String>>();
		List<Map<String, String>> services = DatalakeContext.getMetadataDao().getServices("");
		try {
			for (Map<String, String> service : services) {
				Map<String, String> map = new LinkedHashMap<String, String>();
				map.put("id", service.get("id"));
				map.put("name", service.get("name"));
				map.put("description", service.get("description"));
				map.put("tags", service.get("tags"));
				String route = service.get("route");
				org.codehaus.jackson.JsonNode _route_ = mapper.readTree(route);
				System.err.println(_route_.get("target").get("config").toString());
				map.put("location",
						_route_.get("target").get("config").get("type").getTextValue() + "://"
								+ _route_.get("target").get("config").get("host").getTextValue() + ":"
								+ _route_.get("target").get("config").get("port").getTextValue()
								+ _route_.get("target").get("config").get("path").getTextValue());
				map.put("owner", "admin@rcggs.com");
				response.add(map);
			}
			return ow.writeValueAsString(response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/updateSchema/{op}/{name}", method = RequestMethod.GET)
	public String updateSchema(final @PathVariable String op, final @PathVariable String name) {
		try {
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			Connection con = DriverManager.getConnection("jdbc:hive2://192.168.56.101:10000/default", "hive", "hive");
			Statement stmt = con.createStatement();
			if (op.equals("add")) {
				stmt.execute("ALTER TABLE edi.mckdelimdata_810_stage ADD COLUMNS (" + name + " BIGINT)");
				stmt.execute("ALTER TABLE edi.mckdelimdata_810_stage CHANGE COLUMN " + name + " " + name
						+ " string  AFTER customer_account_id__bill_to_");
			} else if (op.equals("remove")) {

			} else if (op.equals("createnew")) {

			} else {

			}

			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return String.format("{name:%s,value:%s}", op, name);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getSchema/{id}", method = RequestMethod.POST)
	public String getSchema(@RequestBody Object obj) {
		try {
			String type = null;
			String path = null;
			
			System.err.println(obj);
			
			String values = obj.toString().substring(1, obj.toString().length() - 1);
			String[] data = values.split(",");
			String clazz = data[0].split("=")[1];
			if (data.length > 1) {
				type = data[1];
				path = data[3];
			}
			SchemaReader reader = DatalakeContext.getSchemaReaders().get(clazz);
			Map<String, String> pathMap = reader.getPathMap();
			
			Class<ResourceReader<SchemaDef>> instance = (Class<ResourceReader<SchemaDef>>) Class
					.forName(reader.getValue());

			ResourceReader<SchemaDef> schemaReader = instance.newInstance();

			List<SchemaDef> schema = schemaReader.read(path, type, pathMap);
			return ow.writeValueAsString(schema);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/getSchemaReaders/{name}", method = RequestMethod.GET)
	public String getSchemaReaders(@PathVariable String name) {
		try {
			return ow.writeValueAsString(DatalakeContext.getSchemaReaders());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	@RequestMapping(value = "/getMetadata/{id}", method = RequestMethod.POST, produces = "application/json")
	public String getMetadata(@RequestBody Map<String, String> map) {
		try {
			List<Map<String, String>> data = DatalakeContext.getJobDao().getMetadata(map.get("key"));
			return ow.writeValueAsString(data);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/getConnection/{name}/{fields}", method = RequestMethod.GET)
	public String getConnection(@PathVariable final String name, @PathVariable final String fields) {
		try {
			ConnectionConfig connection = DatalakeContext.getConnections().get(name);
			return ow.writeValueAsString(MetadataBuilder.build(datalakeConnectionFactory,
					new AbstractMap.SimpleImmutableEntry<>(name, connection), fields));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/getConnections/{name}", method = RequestMethod.GET)
	public String getConnections(@PathVariable final String name) {

		ExecutorService executor = null;
		try {

			ConcurrentMap<String, ConnectionConfig> connections = DatalakeContext.getConnections();
			ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat("edge-web-thread #%d")
					.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
						@Override
						public void uncaughtException(Thread t, Throwable e) {
							e.printStackTrace();
						}
					}).build();

			executor = Executors.newFixedThreadPool(connections.size(), tf);
			final List<Level> levels = new ArrayList<Level>();
			List<FutureTask<Level>> taskList = new ArrayList<FutureTask<Level>>(connections.size());

			for (final Map.Entry<String, ConnectionConfig> entry : connections.entrySet()) {

				FutureTask<Level> futureTask_1 = new FutureTask<Level>(new Callable<Level>() {
					@Override
					public Level call() {
						return MetadataBuilder.build(datalakeConnectionFactory, entry, null);
					}
				});
				taskList.add(futureTask_1);
				executor.execute(futureTask_1);
			}

			for (int j = 0; j < taskList.size(); j++) {
				FutureTask<Level> futureTask = taskList.get(j);
				Level level = futureTask.get();
				levels.add(level);
			}
			executor.shutdown();

			List<List<Level>> list = new ArrayList<>();

			list.add(levels);

			ConnectionConfig c = new ConnectionConfig();
			Map<String, Object> data = new HashMap<String, Object>();

			c.setHost("http://52.201.45.52");
			c.setPort("3005");
			c.setName("Trifacta AWS");
			c.setPath("");
			c.setType("trifacta");
			c.setProperties(new Properties());
			c.setClazz("");
			c.setUser("");
			c.setPwd("");

			data.put("config", c);

			List<Level> plugins = new LinkedList<>();
			Level p1 = new Level();
			p1.setName("Trifacta");
			p1.setId(UUID.randomUUID().toString());
			p1.setParent("plugins");
			p1.setText("Trifacta");
			p1.setType("trifacta");
			p1.setItemType("plugin");
			p1.setData(data);

			List<Level> plugins1 = new LinkedList<>();
			Level p2 = new Level();
			p2.setName("Trifacta");
			p2.setId(UUID.randomUUID().toString());
			p2.setParent("plugins");
			p2.setText("Trifacta");
			p2.setType("trifacta");
			p2.setItemType("plugin");
			p2.setData(data);

			plugins1.add(p2);

			p1.setChildren(plugins1);

			plugins.add(p1);

			list.add(plugins);

			String json = ow.writeValueAsString(list);

			return json;

		} catch (Exception e) {
			logger.info(Throwables.getStackTraceAsString(e));
			logger.info("Error ocuured " + e.getMessage());
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}

		return null;
	}
}
