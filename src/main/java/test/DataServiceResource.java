package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.ojai.Document;
import org.ojai.DocumentStream;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Throwables;
import com.mapr.db.Admin;
import com.mapr.db.FamilyDescriptor;
import com.mapr.db.MapRDB;
import com.mapr.db.Table;
import com.mapr.db.TableDescriptor;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;
import com.rcggs.datalake.core.model.SchemaDef;

@Path("/enable/api/v1/")
public class DataServiceResource {

	final Logger logger = Logger.getLogger(getClass());

	ObjectMapper mapper = new ObjectMapper();

	Map<String, List<ItemDefinition>> directories = new LinkedHashMap<String, List<ItemDefinition>>();
	Map<String, List<ItemDefinition>> files = new LinkedHashMap<String, List<ItemDefinition>>();

	@POST
	@Path("run")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response run(final Map<String, String> route) {
		logger.info(route);
		try {

			logger.info(route);
			Configuration config = HBaseConfiguration.create();
			// config.set("hbase.zookeeper.property.clientPort", "5181");

			HTable hTable = new HTable(config, "/tables/opcdata");

			Put p = new Put(Bytes.toBytes(route.remove("id")));

			for (Map.Entry<String, String> entry : route.entrySet()) {
				p.add(Bytes.toBytes(entry.getKey()), Bytes.toBytes(entry.getKey()), Bytes.toBytes(entry.getValue()));
				hTable.put(p);
			}

			logger.info("data inserted");

			hTable.close();

		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).entity(e.getMessage()).build();
		}
		return null;
	}

	@POST
	@Path("/maprdb/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public Map<String, List<ItemDefinition>> maprdb(ConnectionConfig config) {

		String path = config.getPath();
		final Map<String, List<ItemDefinition>> map = new HashMap<String, List<ItemDefinition>>();

		final List<ItemDefinition> columnDefs = new ArrayList<ItemDefinition>();
		UserGroupInformation realUser = UserGroupInformation.createRemoteUser("mapr");
		UserGroupInformation.setLoginUser(realUser);
		final Admin admin = MapRDB.newAdmin();
		List<org.apache.hadoop.fs.Path> tables = admin.listTables(new org.apache.hadoop.fs.Path(path));
		System.err.println(String.format("found %s tables in path %s", tables.size(), path));

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		for (final org.apache.hadoop.fs.Path tablePath : admin.listTables(new org.apache.hadoop.fs.Path(path))) {

			// insertDocument(tablePath.getName(), "0", buildDocument());

			TableDescriptor tableDesc = admin.getTableDescriptor(tablePath);
			for (FamilyDescriptor families : tableDesc.getFamilies()) {
				logger.info(families.getName());
				logger.info(families.getJsonFieldPath());
			}

			ObjectMapper mapper = new ObjectMapper();

			Table table = MapRDB.getTable(tablePath);
			DocumentStream documentStream = table.find();

			List<SchemaDef> schemas = new LinkedList<SchemaDef>();

			for (Document document : documentStream) {
				try {
					org.codehaus.jackson.JsonNode json = mapper.readTree(document.asJsonString());
					for (java.util.Iterator<Entry<String, org.codehaus.jackson.JsonNode>> it = json.getFields(); it
							.hasNext();) {
						Entry<String, org.codehaus.jackson.JsonNode> node = it.next();
						SchemaDef schemaDef = new SchemaDef();
						schemaDef.setName(node.getKey());
						schemaDef.setValue("string");
						schemas.add(schemaDef);

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}

			ItemDefinition cd = new ItemDefinition();
			cd.setName(tablePath.getName());
			cd.setItemType("table");
			cd.setType(config.getType());
			cd.setSchema(schemas);
			cd.setData(data);
			columnDefs.add(cd);
		}

		final ItemDefinition idef = new ItemDefinition();
		idef.setName("maprdb");
		// idef.setType(config.getType());
		// idef.setData(dataMap);
		idef.setChildren(columnDefs);

		map.put("maprdb", columnDefs);

		return map;
	}

	// private Map<String, List<ItemDefinition>> start(String baseUri,
	// Map<String, Object> data, ConnectionConfig config)
	// throws IOException {
	// UserGroupInformation realUser =
	// UserGroupInformation.createRemoteUser("mapr");
	// UserGroupInformation.setLoginUser(realUser);
	// Configuration conf = new Configuration();
	// FileSystem fs = FileSystem.get(conf);
	// FileStatus[] status = fs.listStatus(new
	// org.apache.hadoop.fs.Path(baseUri));
	// return browse(baseUri, status, "/", fs, data, config);
	// }

	List<ItemDefinition> children = new ArrayList<>();

	private Map<String, List<ItemDefinition>> browse(String baseUri, FileStatus[] status, String curDirName,
			FileSystem fileSystem, Map<String, Object> data, ConnectionConfig config)
			throws FileNotFoundException, IOException {

		for (int i = 0; i < status.length; i++) {
			FileStatus fileStatus = status[i];
			String path = fileStatus.getPath().getParent().getName() + "/" + fileStatus.getPath().getName();
			System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^6");
			System.out.println(path);

			ItemDefinition idef = new ItemDefinition();
			idef.setName(path);
			idef.setData(data);
			idef.setItemType("directory");
			idef.setType("hdfs2");
			children.add(idef);
			directories.put(config.getPath(), children);
			// FileStatus[] subStatus = fileSystem.listStatus(fileStatus.getPath

			// if (fileStatus.isDirectory()) {
			//
			// ItemDefinition idef = new ItemDefinition();
			// idef.setName(path);
			// idef.setData(data);
			// idef.setItemType("directory");
			// idef.setType("hdfs2");
			// List<ItemDefinition> children = new ArrayList<>();
			// directories.put(path, children);
			// FileStatus[] subStatus =
			// fileSystem.listStatus(fileStatus.getPath());
			// browse(baseUri, subStatus, path, fileSystem, data, config);
			//
			// } else {
			//
			// String parent = fileStatus.getPath().getParent().getName();
			//
			// ItemDefinition idef = new ItemDefinition();
			// idef.setName(path);
			// idef.setData(data);
			// idef.setItemType("file");
			// idef.setType(config.getType());
			// idef.setParent(config.getPath() +
			// fileStatus.getPath().getParent().getName() + "/");
			// List<ItemDefinition> children = new ArrayList<>();
			// children.add(idef);
			// directories.put(parent, children);
			//
			// // items.get(parent).add(idef);
			//
			// // idef.setName(path);
			// // idef.setData(data);
			// // idef.setItemType(fileStatus.isDirectory() ? "directory" :
			// // "file");
			// // idef.setType(config.getType());
			// // idef.setParent(config.getPath());
			//
			// }
		}

		System.out.println(directories);
		System.out.println(files);

		return files;
	}

	@POST
	@Path("/hbase/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public Map<String, List<ItemDefinition>> hbase(final @RequestBody @Valid ConnectionConfig config) {

		String dirname = "/data";
		final Map<String, List<ItemDefinition>> map = new HashMap<String, List<ItemDefinition>>();

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		HBaseAdmin admin = null;
		try {

			System.out.println("==>hbase path is **** " + config.getPath());

			Configuration configuration = HBaseConfiguration.create();
			configuration.set("hbase.table.namespace.mappings", "*:/");
			admin = new HBaseAdmin(configuration);
			logger.info("successfully created hbase admin " + admin);

			HTableDescriptor[] tableDescriptor = null;

			if ("".equals(config.getPath())) {
				tableDescriptor = admin.listTables();
			} else {
				tableDescriptor = admin.listTables(config.getPath());
			}
			List<ItemDefinition> columnDefs = new ArrayList<ItemDefinition>();

			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("config", config);

			for (int i = 0; i < tableDescriptor.length; i++) {

				System.err.println("hbase table " + tableDescriptor[i].getNameAsString());

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
			map.put(config.getHost(), columnDefs);

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

		return map;
	}

	@POST
	@Path("/streams/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public Map<String, List<ItemDefinition>> streams(final @RequestBody @Valid ConnectionConfig config) {

		final Map<String, List<ItemDefinition>> map = new HashMap<String, List<ItemDefinition>>();

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		List<ItemDefinition> idefs = new ArrayList<ItemDefinition>();

		Configuration conf = new Configuration();
		com.mapr.streams.Admin streamAdmin;
		try {
			// streamAdmin = Streams.newAdmin(conf);
			// int count = streamAdmin.countTopics("/default_stream");
			// for (int i = 0; i < count; i++) {
			// System.out.println("");
			// }
			// streamAdmin.close();

			ProcessBuilder builder = new ProcessBuilder("/usr/bin/maprcli", "stream", "topic", "list", "-path",
					"/default_stream");
			final Process process = builder.start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			br.readLine();
			while ((line = br.readLine()) != null) {
				if (!line.trim().startsWith("partition")) {
					System.out.println();
					ItemDefinition cd = new ItemDefinition();
					cd.setName(line.split("\\s+")[3]);
					cd.setItemType("table");
					cd.setType(config.getType());
					cd.setData(data);
					idefs.add(cd);
				}
			}

			map.put(config.getHost(), idefs);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	@POST
	@Path("/drill/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public Response drill(final @RequestBody @Valid ConnectionConfig config) {

		final Map<String, List<ItemDefinition>> map = new HashMap<String, List<ItemDefinition>>();

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		ItemDefinition parentDef = new ItemDefinition();
		parentDef.setName(config.getHost());
		parentDef.setItemType("host");
		parentDef.setData(data);

		try {

			Class.forName("org.apache.drill.jdbc.Driver");
			Connection conn = DriverManager.getConnection(
					"jdbc:drill:zk=" + config.getHost() + ":5181/drill/demo_mapr_com-drillbits", "admin", "admin");
			Statement st = conn.createStatement();
			List<ItemDefinition> dbDefs = new ArrayList<ItemDefinition>();

			ResultSet rs = st.executeQuery("show databases");
			while (rs.next()) {
				ItemDefinition debDef = new ItemDefinition();
				debDef.setName(rs.getString(1));
				debDef.setItemType("database");
				debDef.setType(config.getType());
				debDef.setData(data);

				List<ItemDefinition> tableDefs = new ArrayList<ItemDefinition>();

				Statement st1 = conn.createStatement();
				ResultSet rs1 = st1.executeQuery(
						"SELECT TABLE_SCHEMA, TABLE_NAME, TABLE_TYPE FROM INFORMATION_SCHEMA.`TABLES` where TABLE_SCHEMA='"
								+ rs.getString(1) + "' ORDER BY TABLE_NAME DESC");
				while (rs1.next()) {
					ItemDefinition tableDef = new ItemDefinition();
					tableDef.setName(rs1.getString(2));
					tableDef.setItemType("database");
					tableDef.setType(config.getType());
					tableDef.setData(data);
					tableDefs.add(tableDef);
				}

				if (tableDefs.size() > 0) {
					debDef.setChildren(tableDefs);
					dbDefs.add(debDef);
				}

			}

			map.put(parentDef.getName(), dbDefs);

			return Response.status(200).entity(mapper.writeValueAsString(map)).build();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@POST
	@Path("/hdfs/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public Response hdfs(final @RequestBody @Valid ConnectionConfig config) {

		String dirname = config.getPath();

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		try {

			UserGroupInformation realUser = UserGroupInformation.createRemoteUser("mapr");
			UserGroupInformation.setLoginUser(realUser);
			Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(conf);
			FileStatus[] status = fs.listStatus(new org.apache.hadoop.fs.Path(config.getPath()));
			Map<String, List<ItemDefinition>> map = new LinkedHashMap<String, List<ItemDefinition>>();

			for (int i = 0; i < status.length; i++) {
				FileStatus fileStatus = status[i];
				String path = "/"+fileStatus.getPath().getParent().getName() + "/" + fileStatus.getPath().getName();

				ItemDefinition idef = new ItemDefinition();
				idef.setName(path);
				idef.setData(data);
				idef.setItemType("directory");
				idef.setType("hdfs2");
				children.add(idef);
				map.put(config.getPath(), children);

			}

			System.out.println("#########################################");
			System.out.println(map);

			return Response.status(200).entity(mapper.writeValueAsString(map)).build();
		} catch (IOException e) {
			logger.error(Throwables.getStackTraceAsString(e));
			e.printStackTrace();
		}

		return null;
	}

	@POST
	@Path("/hive/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ResponseBody
	public Map<String, List<ItemDefinition>> hive(final @RequestBody @Valid ConnectionConfig config) {

		String dirname = "/data";
		final Map<String, List<ItemDefinition>> map = new HashMap<String, List<ItemDefinition>>();

		final Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		Statement stmt;

		try {

			Class.forName("org.apache.hive.jdbc.HiveDriver");
			Connection connection = DriverManager
					.getConnection("jdbc:hive2://" + config.getHost() + ":10000/default" + "", "mapr", "mapr");

			stmt = connection.createStatement();
			ResultSet resultSet = stmt.executeQuery("show databases");
			ItemDefinition parentDef = new ItemDefinition();
			parentDef.setName("databases");
			parentDef.setItemType("host");
			parentDef.setData(data);

			List<ItemDefinition> dbDefs = new ArrayList<ItemDefinition>();
			List<String> databases = new ArrayList<String>();

			while (resultSet.next()) {
				String dbname = resultSet.getString(1);
				databases.add(dbname);
			}
			resultSet.close();
			stmt.close();
			for (String db : databases) {
				try {

					ItemDefinition dbDef = new ItemDefinition();
					dbDef.setName(db);
					dbDef.setItemType("database");
					dbDef.setType(config.getType());
					dbDef.setData(data);
					dbDefs.add(dbDef);

					Statement stmt1 = connection.createStatement();
					ResultSet resultSet1 = stmt1.executeQuery("show tables in " + db);
					List<ItemDefinition> tableDefs = new ArrayList<ItemDefinition>();

					List<SchemaDef> schemas = new LinkedList<SchemaDef>();
					while (resultSet1.next()) {
						ItemDefinition tableDef = new ItemDefinition();
						tableDef.setName(resultSet1.getString(1));
						tableDef.setItemType("table");
						tableDef.setType(config.getType());
						tableDef.setData(data);
						tableDefs.add(tableDef);

						// default skip columns
						List<ItemDefinition> columnDefs = null;
						String showColumns = config.getProperties().getProperty("show.columns");

						Statement stmt2 = connection.createStatement();
						ResultSet resultSet2 = stmt2.executeQuery("describe " + db + "." + resultSet1.getString(1));
						columnDefs = new ArrayList<ItemDefinition>();
						Map<String, String> _map_;

						while (resultSet2.next()) {
							_map_ = new HashMap<String, String>();
							_map_.put(resultSet2.getString(1), resultSet2.getString(2));
							ItemDefinition columnDef = new ItemDefinition();
							columnDef.setName(resultSet2.getString(1));
							columnDef.setType(config.getType());
							columnDef.setItemType("column");
							columnDef.setData(data);
							columnDefs.add(columnDef);
							SchemaDef schema = new SchemaDef();
							schema.setName(resultSet2.getString(1));
							schema.setValue(resultSet2.getString(2));
							schema.setChecked(true);
							schemas.add(schema);
						}
						resultSet2.close();
						stmt2.close();

						tableDef.setSchema(schemas);
						tableDef.setChildren(columnDefs);
					}
					resultSet1.close();
					stmt1.close();
					dbDef.setChildren(tableDefs);

				} catch (Exception e) {
					logger.error(Throwables.getStackTraceAsString(e));
					logger.error("Unable to describe database: " + db, e);
				}
				parentDef.setChildren(dbDefs);
				map.put(parentDef.getName(), dbDefs);
			}

		} catch (Exception e) {
			logger.error(Throwables.getStackTraceAsString(e));
			e.printStackTrace();
		}

		return map;
	}
}
