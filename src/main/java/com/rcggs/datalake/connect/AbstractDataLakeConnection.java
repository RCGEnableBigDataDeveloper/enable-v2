package com.rcggs.datalake.connect;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.rcggs.datalake.core.model.ConnectionConfig;

public abstract class AbstractDataLakeConnection {

	protected AbstractDataLakeConnection(ConnectionConfig config) {

	}

	protected List<Map<String, String>> map(ResultSet rs) throws SQLException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		ResultSetMetaData meta = rs.getMetaData();
		while (rs.next()) {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				String key = meta.getColumnName(i);
				String value = rs.getString(key);
				map.put(key, value);
			}
			list.add(map);
		}
		return list;
	}

	public String id() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
