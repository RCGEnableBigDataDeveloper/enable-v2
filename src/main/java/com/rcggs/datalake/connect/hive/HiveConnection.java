package com.rcggs.datalake.connect.hive;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivilegedExceptionAction;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginContext;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.elasticsearch.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;
import com.rcggs.datalake.core.model.SchemaDef;

public class HiveConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	final Logger logger = LoggerFactory.getLogger(getClass());

	java.sql.Connection connection;
	ConnectionConfig config;

	public HiveConnection(ConnectionConfig config) {
		super(config);
		try {
			this.config = config;
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			String driverProperties = "";

			if (config.getProperties() != null)
				driverProperties = config.getProperties().getProperty("driver.properties");

			final String url = "jdbc:" + config.getType() + "://" + config.getHost() + ":" + config.getPort()
					+ (config.getPath() != null && config.getPath().length() > 0 ? ("/" + config.getPath()) : "")
					+ (driverProperties != null ? driverProperties : "");
			logger.info("hive url connection: {}", url);
			Configuration conf = new Configuration();

			if (config.getProperties() != null && config.getProperties().getProperty("kerberos.principal") != null) {

				logger.info("hive krb user " + config.getProperties().getProperty("kerberos.principal") + ":"
						+ config.getProperties().getProperty("kerberos.keytab"));

				conf.set("hadoop.security.authentication", "Kerberos");
				UserGroupInformation.setConfiguration(conf);
				UserGroupInformation ugi = null;
				try {
					Class.forName("org.apache.hive.jdbc.HiveDriver");
					ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(
							config.getProperties().getProperty("kerberos.principal"),
							config.getProperties().getProperty("kerberos.keytab"));
					ugi.doAs(new PrivilegedExceptionAction<Void>() {
						public Void run() throws Exception {
							connection = DriverManager.getConnection(url);
							return null;
						}
					});
				} catch (IOException | InterruptedException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else {

				String username = config.getUser();
				char[] password = config.getPwd().toCharArray();

				LoginContext lc = kinit(username, password);
				UserGroupInformation.loginUserFromSubject(lc.getSubject());

				connection = DriverManager.getConnection(url);
			}
		} catch (Exception e) {
			logger.error(Throwables.getStackTraceAsString(e));
			logger.error("Unable to establish a connection with hive", e);

		}
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(String name) {

		Statement stmt;
		Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		try {

			if (true) {
				return showTables(data);
			}

			if (config.getProperties() != null) {
				final String principal = config.getProperties().getProperty("kerberos.principal");
				if (principal != null && !"".equals(principal)) {
					name = config.getProperties().getProperty("kerberos.principal").substring(0,
							principal.indexOf("@"));
				}
			}

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

					// default skip tables
					String showTables = config.getProperties() != null
							? config.getProperties().getProperty("show.tables") : "";
					if ("true".equalsIgnoreCase(showTables)) {
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
							if ("true".equalsIgnoreCase(showColumns)) {
								Statement stmt2 = connection.createStatement();
								ResultSet resultSet2 = stmt2
										.executeQuery("describe " + db + "." + resultSet1.getString(1));
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

							}
							tableDef.setSchema(schemas);
							tableDef.setChildren(columnDefs);
						}
						resultSet1.close();
						stmt1.close();
						dbDef.setChildren(tableDefs);
					}
				} catch (Exception e) {
					logger.error(Throwables.getStackTraceAsString(e));
					logger.error("Unable to describe database: " + db, e);
				}
				parentDef.setChildren(dbDefs);
				map.put(parentDef, dbDefs);
			} // end for loop

		} catch (Exception e) {
			logger.error(Throwables.getStackTraceAsString(e));
			logger.error("Unable to get list of databases", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
				}
			}
		}

		return map;
	}

	private Map<ItemDefinition, List<ItemDefinition>> showTables(Map<String, Object> data) throws Exception {

		Runtime rt = Runtime.getRuntime();
		String[] commands = { "/usr/bin/hive", "-e", "\"show databases\"" };
		Process proc = rt.exec(commands);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

		BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

		// read the output from the command
		String s = null;

		Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();

		List<ItemDefinition> dbDefs = new ArrayList<ItemDefinition>();
		List<String> databases = new ArrayList<String>();
		ItemDefinition parentDef = new ItemDefinition();
		parentDef.setName(config.getHost());

		while ((s = stdInput.readLine()) != null) {

			System.out.println(s);

			if (s.indexOf(" ") == -1) {

				ItemDefinition dbDef = new ItemDefinition();
				dbDef.setName(s);
				dbDef.setItemType("database");
				dbDef.setType(config.getType());
				dbDef.setData(data);
				dbDefs.add(dbDef);
			}

		}

		while ((s = stdError.readLine()) != null) {
			System.out.println(s);
		}

		proc.waitFor();

		stdInput.close();
		stdInput.close();

		parentDef.setChildren(dbDefs);
		map.put(parentDef, dbDefs);

		return map;
	}
}
