package com.rcggs.datalake.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rcggs.datalake.core.model.Metric;
import com.rcggs.datalake.pool.DataSource;

public class MetricsDao extends AbstractDao {

	public List<Map<String, Object>> getMetrics(final String name) {
		String query = "select * from dl_metrics order by id desc limit 10";
		List<Map<String, Object>> metrics = new ArrayList<Map<String, Object>>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			connection = DataSource.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(query);
			while (rs.next()) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("id", rs.getString(2));
				data.put("srcId", rs.getString(3));
				data.put("targetId", rs.getString(4));
				data.put("name", rs.getString(5));
				data.put("type", rs.getString(6));
				data.put("description", rs.getString(7));
				data.put("message", rs.getString(8));
				data.put("time", rs.getString(9));
				data.put("dataSetName", rs.getString(10));
				data.put("fileTimeStamp", rs.getString(11));
				data.put("totalRows", rs.getString(12));
				data.put("totalColumns", rs.getString(13));
				data.put("totalFiles", rs.getString(14));
				data.put("routeId", rs.getString(15));
				data.put("nodeId", rs.getString(16));
				data.put("fileSize", rs.getString(17));
				data.put("status", rs.getString(18));
				data.put("messageType", rs.getString(19));
				metrics.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, statement, connection);
		}

		return metrics;
	}

	public int saveMetric(final Metric metric) {
		Connection connection = null;
		PreparedStatement preparedStmt = null;
		int updated = 0;
		try {
			connection = getConnection();
			String query = "insert into dl_metrics(id,srcId,targetId,name,type,description,message,time,dataSetName,fileTimeStamp,totalRows,totalColumns,totalFiles,routeId,nodeId,fileSize,status,messageType)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, metric.getId());
			preparedStmt.setString(2, metric.getSrcId());
			preparedStmt.setString(3, metric.getTargetId());
			preparedStmt.setString(4, metric.getName());
			preparedStmt.setString(5, metric.getType());
			preparedStmt.setString(6, metric.getDescription());
			preparedStmt.setString(7, metric.getMessage());

			preparedStmt.setString(8, String.valueOf(metric.getTime()));
			preparedStmt.setString(9, metric.getDataSetName());
			preparedStmt.setString(10, metric.getFileTimeStamp());
			preparedStmt.setInt(11, metric.getTotalRows());
			preparedStmt.setInt(12, metric.getTotalColumns());
			preparedStmt.setInt(13, metric.getTotalFiles());
			preparedStmt.setString(14, metric.getRouteId());
			preparedStmt.setString(15, metric.getNodeId());
			preparedStmt.setString(16, metric.getFileSize());
			preparedStmt.setString(17, metric.getStatus());
			preparedStmt.setString(18, metric.getMessageType());

			updated = preparedStmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, preparedStmt, connection);
		}
		return updated;
	}

	public void saveMetrics(final List<Metric> metrics) {

		for (Metric metric : metrics) {
			saveMetric(metric);
		}
	}
}
