package com.rcggs.datalake.core.model;

public class TableDefinition {

	private String tableName;
	private String createTableCmd;
	private String loadTableCmd;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getCreateTableCmd() {
		return createTableCmd;
	}

	public void setCreateTableCmd(String createTableCmd) {
		this.createTableCmd = createTableCmd;
	}

	public String getLoadTableCmd() {
		return loadTableCmd;
	}

	public void setLoadTableCmd(String loadTableCmd) {
		this.loadTableCmd = loadTableCmd;
	}
}
