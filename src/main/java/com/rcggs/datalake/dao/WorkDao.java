package com.rcggs.datalake.dao;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public class WorkDao extends AbstractDao {
	
	final  Logger logger = LoggerFactory.getLogger(getClass());

	final static ObjectWriter ow = new ObjectMapper().writer();
	final static ObjectMapper mapper = new ObjectMapper();

	String toJson(final Object data) {
		try {
			return ow.writeValueAsString(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int saveWork(String id, String route) {

		Connection conn = null;
		PreparedStatement preparedStmt = null;
		try {
			conn = getConnection();

			Statement statement = conn.createStatement();
			ResultSet existingWork = statement.executeQuery("SELECT * from dl_work where id = '" + id + "'");
			route = route.replaceAll("%", "");
			route = URLDecoder.decode(route, "UTF-8");
			
			if (!existingWork.next()) {
				String query = "insert into dl_work(id,model) values(?,?)";
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, id);
				preparedStmt.setString(2, route);
				return preparedStmt.executeUpdate();
			} else {
				String query = "update dl_work set model = ? where id = ?";
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1,route);
				preparedStmt.setString(2, id);
				return preparedStmt.executeUpdate();
			}

		} catch (Exception e) {
			logger.info(Throwables.getStackTraceAsString(e));
			e.printStackTrace();
		} finally {
			close(null, preparedStmt, conn);
		}

		return -1;
	}

	@SuppressWarnings("resource")
	public String getWork(final String id) {

		Connection connection = null;
		Statement stmt = null;
		String model = null;
		ResultSet rs = null;
		String workQuery = "select model from dl_work where id = '" + id + "'";
		String scheduledWorkQuery = "select model from dl_wf_layout where id = '" + id + "'";

		try {
			connection = getConnection();
			stmt = connection.createStatement();
			//check if its a scheduled work
			rs = stmt.executeQuery(scheduledWorkQuery);
			while (rs.next()) {
				model = rs.getString(1);
			}
			
			if (model == null)  {
				//check if realtime work
				rs = stmt.executeQuery(workQuery);
				while (rs.next()) {
					model = rs.getString(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, stmt, connection);
		}
		return model;
	}
}
