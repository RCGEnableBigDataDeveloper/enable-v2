package com.rcggs.datalake.connect.rdbms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.rcggs.datalake.configuration.DatalakeContext;
import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;
import com.rcggs.datalake.core.model.SchemaDef;

public class SQLConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	final static Logger log = LoggerFactory.getLogger(SQLConnection.class);
	java.sql.Connection connection;
	ConnectionConfig config;
	// check if schema already built
	Map<ItemDefinition, List<ItemDefinition>> cachedSchemaMap;

	public static String getDriver(final String type) {
		switch (type) {
		case "mysql":
			return "com.mysql.jdbc.Driver";
		case "oracle":
			return "oracle.jdbc.driver.OracleDriver";
		case "phoenix":
			return "org.apache.phoenix.jdbc.PhoenixDriver";
		case "as400":
			return "com.ibm.as400.access.AS400JDBCDriver";
		case "sap":
			return "com.sap.db.jdbc.Driver";
		case "openedge":
			return "com.ddtek.jdbc.openedge.OpenEdgeDriver";
		case "sqlserver":
			return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		case "cache":
			return "com.intersys.jdbc.CacheDriver";
		default:
			log.error("Invalid or unknown database type: " + type);
		}
		return null;
	}

	public static String getUrl(final String type, final String host, final String port, final String db,
			final String properties, String connectionType) {
		switch (type) {
		case "mysql":
			return String.format("jdbc:mysql://%s:%s/%s", host, port, db);
		case "oracle":
			if (null != connectionType) {
				if ("NAMESERVICE".equals(connectionType)) {
					return String.format("jdbc:oracle:thin:@%s:%s/%s", host, port, db);
				} else {
					return String.format("jdbc:oracle:thin:@%s:%s:%s", host, port, db);
				}
			} else {
				return String.format("jdbc:oracle:thin:@%s:%s:%s", host, port, db);
			}
		case "phoenix":
			return String.format("jdbc:phoenix:%s:%s/%s", host, port, db);
		case "as400":
			if (null != properties)
				return String.format("jdbc:as400://%s", host);
			else
				return String.format("jdbc:as400://%s", host);
		case "sap":
			return String.format("jdbc:sap://%s:%s/%s", host, port, db);
		case "openedge":
			return String.format("jdbc:datadirect:openedge://%s:%s;%s", host, port, properties);
		case "sqlserver":
			return String.format("jdbc:sqlserver://%s:%s;database=%s", host, port, db);
		case "cache":
			return String.format("jdbc:Cache://%s:%s/%s", host, port, db);
		default:
			log.error("Invalid or unknown database type: " + type);
		}
		return null;
	}
	
	public static void main(String[] args) {
	String s = 	getUrl("as400", "MBPROD.reyesholdings.com;libraries=MBPROD", "9090", "PRDATA", null, null);
	System.out.println(s);
	}

	public SQLConnection(ConnectionConfig config) throws Exception {
		super(config);
		try {
			this.config = config;

			cachedSchemaMap = readSchema();

			// not chached, then build it
			if (cachedSchemaMap == null) {
				try {
					Class.forName(getDriver(config.getType()));
				} catch (ClassNotFoundException e) {
					log.error("Unable to load driver", e);
					log.error(e.getMessage());
					log.error(Throwables.getStackTraceAsString(e));
					e.printStackTrace();
					throw e;
				}

				String driverProperties = null;
				String connectionTypes = null;
				if (null != config.getProperties()) {
					driverProperties = config.getProperties().getProperty("driver.properties");
					connectionTypes = config.getProperties().getProperty("connection.type");
				}

				String url = getUrl(config.getType(), config.getHost(), config.getPort(), config.getPath(),
						driverProperties, connectionTypes);

				System.err.println("AS400 URL IS : \n" + url);
				System.err.println(config.getUser() + " : " + config.getPwd());
				
				
				//	E1GLOBALB : E1GL0B@L81


				connection = DriverManager.getConnection(url, config.getUser(), config.getPwd());

				// Class.forName("com.ibm.as400.access.AS400JDBCDriver");
				// connection =
				// DriverManager.getConnection("jdbc:as400://MBPROD.reyesholdings.com;libraries=MBPROD",
												//jdbc:as400://MBPROD.reyesholdings.com;libraries=MBPROD
				                                
				// "E1GLOBALBI", "E1GL0B@L81");
			}

		} catch (SQLException e) {
			log.error("Unable to get a connection", e);
			log.error(e.getMessage());
			log.error(Throwables.getStackTraceAsString(e));
			throw e;
		}
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(final String name) {

		log.info("==> call to describe " + config.getName());

		// if built return it
		if (cachedSchemaMap == null) {

			cachedSchemaMap = new HashMap<ItemDefinition, List<ItemDefinition>>();
			String tableqry = null;
			String columnqry = null;

			if (config.getType().equals("sqlserver")) {
				if (tableqry == null)
					tableqry = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' OR TABLE_TYPE = 'VIEW' ORDER BY TABLE_NAME";
				if (columnqry == null)
					columnqry = "SELECT column_name, data_type, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '%s'";
			} else if (config.getType().equals("sap")) {
				if (tableqry == null)
					tableqry = "Select TABLE_NAME from TABLES WHERE SCHEMA_NAME='" + config.getPath() + "'";
				if (name != null) {
					log.debug("==> found arg for sap connection " + name);
					tableqry = tableqry + "AND TABLE_NAME IN (" + name + ")";
				}
				if (columnqry == null)
					columnqry = "SELECT COLUMN_NAME, DATA_TYPE_NAME, LENGTH, SCALE FROM  TABLE_COLUMNS WHERE TABLE_NAME = '%s'";
			} else if (config.getType().equals("mysql")) {
				if (tableqry == null)
					tableqry = "SHOW TABLES";
				if (columnqry == null)
					columnqry = "DESCRIBE %s";
			} else if (config.getType().equals("oracle")) {
				if (tableqry == null)
					tableqry = "SELECT table_name FROM user_tables where not tablespace_name='SYSAUX' and  table_name not like '%$%'";
				if (columnqry == null)
					columnqry = "select distinct(column_name), data_type, data_length, data_precision, data_scale from all_tab_columns where table_name='%s' order by column_name";
			} else if (config.getType().equals("phoenix")) {
				tableqry = "select distinct(table_name) from system.CATALOG where table_name not in('STATS', 'CATALOG','FUNCTION','SEQUENCE')";
				columnqry = "select column_name, data_type,column_size, decimal_digits, num_prec_radix from system.CATALOG where table_name = '%s' and column_name is not null";
			} else if (config.getType().equals("as400")) {
				if (config.getPath() != null || !"".equals(config.getPath().trim())) {
					String schemaName = config.getPath();
					tableqry = "SELECT TABLE_NAME FROM qsys2.systables WHERE TABLE_SCHEMA = '" + schemaName
							+ "' and table_type = 'P' and file_type = 'D' and colcount > 1";
					columnqry = "SELECT COLUMN_NAME, DATA_TYPE, LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE FROM qsys2.syscolumns WHERE TABLE_SCHEMA = '"
							+ schemaName + "' AND TABLE_NAME ='%s' ORDER BY ORDINAL_POSITION";
				} else {
					log.error("path should not be empty or null.  Please check as400 connection configuration.");
				}
			} else if (config.getType().equals("sap")) {
				tableqry = config.getProperties().getProperty("table.query");
				columnqry = "SELECT column_name,data_type_name,length,offset,scale  FROM TABLE_COLUMNS WHERE TABLE_NAME = '%s' and column_name is not null";
			} else if (config.getType().equals("openedge")) {
				tableqry = "select TBL from sysprogress.SYSTABLES where tbltype = 'T'";
				columnqry = "select col, coltype, width, 0, scale from sysprogress.SYSCOLUMNS where TBL = '%s'";
			} else if (config.getType().equals("cache")) {
				tableqry = ".....";
				columnqry = ".....";
			} else {
				log.error("invalid database type: " + config.getType());
			}

			if (config.getProperties() != null) {
				if (config.getProperties().get("table.query") != null)
					tableqry = config.getProperties().getProperty("table.query");
				if (config.getProperties().get("column.query") != null)
					columnqry = config.getProperties().getProperty("column.query");
			}

			Statement stmt = null;
			ResultSet tblrs = null;
			ResultSet colrs = null;
			Statement stmt1 = null;

			boolean pagedResult = false;
			int pageSize = 0;
			int currentPage = 0;

			if (config.getProperties() != null && config.getProperties().getProperty("max.results") != null
					&& !"".equals(config.getProperties().getProperty("max.results"))) {
				pagedResult = true;
				pageSize = Integer.parseInt(config.getProperties().getProperty("max.results"));
			}

			log.info("==> max results set to  " + pageSize + " for connection " + config.getName());

			try {
				stmt = this.connection.createStatement();
				log.debug("==> table query for " + name + " is " + tableqry);
				tblrs = stmt.executeQuery(tableqry);
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("config", config);
				List<ItemDefinition> tableDefs = new ArrayList<ItemDefinition>();

				while (tblrs.next()) {
					log.debug("==> found table " + tblrs.getString(1));
					stmt1 = this.connection.createStatement();

					List<ItemDefinition> columnDefs = new ArrayList<ItemDefinition>();

					List<SchemaDef> schemas = new LinkedList<SchemaDef>();
					colrs = stmt1.executeQuery(String.format(columnqry, tblrs.getString(1)));

					int colcnt = 0;
					while (colrs.next()) {

						// ItemDefinition columnDef = new ItemDefinition();
						// columnDef.setName(rs1.getString(1));
						// columnDef.setType(rs1.getString(2));
						// columnDef.setLength(rs1.getString(3));
						// columnDef.setPrecision(rs1.getString(4));
						// columnDef.setScale(rs1.getString(5));
						// columnDef.setData(data);
						// columnDef.setItemType("column");
						// columnDefs.add(columnDef);
						SchemaDef schema = new SchemaDef();
						schema.setName(colrs.getString(1));
						schema.setValue(colrs.getString(2));
						schema.setChecked(true);
						schemas.add(schema);
						colcnt++;
					}

					// need to close the inner result set and statement.
					colrs.close();
					stmt1.close();

					if (pagedResult) {
						currentPage++;
						if (currentPage > pageSize) {
							break;
						}
					}

					ItemDefinition tableDef = new ItemDefinition();
					tableDef.setName(tblrs.getString(1));
					tableDef.setItemType("table");
					tableDef.setType(config.getType());
					tableDef.setSchema(schemas);
					tableDef.setChildren(columnDefs);
					tableDef.setData(data);
					tableDefs.add(tableDef);
				}

				ItemDefinition dbDef = new ItemDefinition();

				String path = config.getPath();
				if (path.indexOf(";") != -1)
					path = path.substring(0, path.indexOf(";"));

				dbDef.setName(path);
				dbDef.setItemType("database");
				dbDef.setType(config.getType());
				dbDef.setData(data);
				cachedSchemaMap.put(dbDef, tableDefs);

				// save schema if necessary
				saveSchemaFile(cachedSchemaMap);

			} catch (SQLException e) {
				log.error(e.getMessage());
				log.error(Throwables.getStackTraceAsString(e));
				e.printStackTrace();
			} finally {
				try {
					if (tblrs != null)
						tblrs.close();
					if (colrs != null)
						colrs.close();
					if (stmt1 != null)
						stmt1.close();
					if (stmt != null)
						stmt.close();
					this.connection.close();
				} catch (SQLException e) {
					log.error(e.getMessage());
					log.error(Throwables.getStackTraceAsString(e));
					e.printStackTrace();
				}
			}
		}

		return cachedSchemaMap;
	}

	@SuppressWarnings("unchecked")
	private Map<ItemDefinition, List<ItemDefinition>> readSchema() {
		String schemaDir = DatalakeContext.getProperty("schema.ser.dir");
		String fileName = schemaDir + "/" + config.getHost() + "_" + config.getPath() + "_schema.ser";
		File schemaFile = new File(fileName);
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
		Map<ItemDefinition, List<ItemDefinition>> map = null;

		if (schemaFile.exists()) {
			try {
				fileIn = new FileInputStream(fileName);
				in = new ObjectInputStream(fileIn);
				map = (Map<ItemDefinition, List<ItemDefinition>>) in.readObject();
			} catch (ClassNotFoundException | IOException e) {
				log.warn("unable to find {}. Creating the schema file: {}", fileName, e);
				log.error(e.getMessage());
				log.error(Throwables.getStackTraceAsString(e));
			} finally {
				if (in != null)
					try {
						in.close();
					} catch (IOException e) {
					}
				if (fileIn != null)
					try {
						fileIn.close();
					} catch (IOException e) {
					}
			}
		}
		return map;
	}

	/**
	 * Save schema if doesn't exists
	 *
	 * @param map
	 */
	private void saveSchemaFile(Map<ItemDefinition, List<ItemDefinition>> map) {
		String schemaDir = DatalakeContext.getProperty("schema.ser.dir");
		String fileName = schemaDir + "/" + config.getHost() + "_" + config.getPath() + "_schema.ser";
		File schemaFile = new File(fileName);
		ObjectOutputStream out = null;
		if (!schemaFile.exists()) {
			try {
				out = new ObjectOutputStream(new FileOutputStream(schemaFile));
				out.writeObject(map);
			} catch (IOException e) {
				log.warn("unable to save {}. schema file", fileName);
				log.error(e.getMessage());
				log.error(Throwables.getStackTraceAsString(e));
			} finally {
				if (out != null)
					try {
						out.close();
					} catch (IOException e) {

					}
			}
		}
	}
}