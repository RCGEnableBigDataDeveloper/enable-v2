package com.rcggs.datalake.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;

public class JobDao extends AbstractDao {

	final Logger logger = Logger.getLogger(getClass());

	final static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	final static ObjectMapper mapper = new ObjectMapper();

	public int updateStatistics(final String route, final String data) {

		Connection conn = null;

		try {
			conn = getConnection();
			PreparedStatement preparedStmt = conn.prepareStatement("insert into dl_statistics(id,route) values(?,?)");
			preparedStmt.setString(1, route);
			preparedStmt.setString(2, data);
			return preparedStmt.executeUpdate();
			
		} catch (Exception e) {

		} finally {
			close(null, null, conn);
		}
		return 0;
	}

	public String getStatistics(final String id, final int count) {
		Connection conn = null;
		try {

			List<String> statsList = new ArrayList<String>();
			String stats = null;
			conn = getConnection();
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery("select id, route from dl_statistics where id='" + id + "'");
			while (rs.next()) {
				stats = rs.getString(2);
				statsList.add(stats.substring(1, stats.length() - 1).replaceAll("\\\\", ""));
			}

			if (stats == null || statsList.size() < count)
				return null;

			return StringUtils.join(statsList, ",");

		} catch (Exception e) {
			logger.error(Throwables.getStackTraceAsString(e));
			e.printStackTrace();
			return "{\"id\":\"" + id + "\",\"status\":\"failed to get stats\"}";

		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public int updateJob(final String name, final String status) {

		Connection connection = null;
		PreparedStatement preparedStmt = null;
		try {
			connection = getConnection();
			String query = "update dl_job set status = ? where id = ?";
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, status);
			preparedStmt.setString(2, name);
			return preparedStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, preparedStmt, connection);
		}
		return 0;
	}

	public int saveMetadata(final String key, final String metadata, final String origin, final String destination,
			final String user, final long timestamp) {

		Connection connection = null;
		PreparedStatement preparedStmt = null;
		Statement st = null;
		ResultSet rs = null;
		String query = null;

		try {
			connection = getConnection();
			st = connection.createStatement();
			rs = st.executeQuery("select * from dl_metadata where id='" + key + "'");

			if (rs.next()) {

				query = "update dl_metadata set id=?,data=?,origin=?,destination=?,usr=?,timestamp=? where id=?";
				preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1, String.valueOf(key));
				preparedStmt.setString(2, String.valueOf(metadata));
				preparedStmt.setString(3, String.valueOf(origin));
				preparedStmt.setString(4, String.valueOf(destination));
				preparedStmt.setString(5, String.valueOf(user));
				preparedStmt.setTimestamp(6, new Timestamp(timestamp));
				preparedStmt.setString(7, String.valueOf(key));

			} else {
				query = "insert into dl_metadata(id,data,origin,destination,usr,timestamp)values(?,?,?,?,?,?)";
				preparedStmt = connection.prepareStatement(query);
				preparedStmt.setString(1, String.valueOf(key));
				preparedStmt.setString(2, String.valueOf(metadata));
				preparedStmt.setString(3, String.valueOf(origin));
				preparedStmt.setString(4, String.valueOf(destination));
				preparedStmt.setString(5, String.valueOf(user));
				preparedStmt.setTimestamp(6, new Timestamp(timestamp));
			}

			return preparedStmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, preparedStmt, connection);
		}
		return 0;
	}

	public List<Map<String, String>> getMetadata(final String key) {

		Connection connection = null;
		Statement stmt = null;
		List<Map<String, String>> metadata = new ArrayList<Map<String, String>>();
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			String query = "select id, data from dl_metadata where id='" + key + "'";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Map<String, String> data = new HashMap<String, String>();
				data.put("id", rs.getString(1));
				data.put("data", rs.getString(2));
				metadata.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, stmt, connection);
		}
		return metadata;
	}

	public int saveJob(final Map<String, Object> data) {

		Connection connection = null;
		PreparedStatement preparedStmt = null;
		try {
			connection = getConnection();
			String query = "insert into dl_job(id,name,owner,grp,active,created,modified,startdate,status)values(?,?,?,?,?,?,?,?,?)";
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, String.valueOf(data.get("id")));
			preparedStmt.setString(2, String.valueOf(data.get("name")));
			preparedStmt.setString(3, "");
			preparedStmt.setString(4, "");
			preparedStmt.setBoolean(5, true);
			preparedStmt.setDate(6, new Date(System.currentTimeMillis()));
			preparedStmt.setDate(7, new Date(System.currentTimeMillis()));
			preparedStmt.setString(8, String.valueOf(data.get("date")));
			preparedStmt.setString(9, "pending");
			return preparedStmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, preparedStmt, connection);
		}
		return 0;
	}

	public List<Map<String, String>> getJobKeys(final String name, final String job) {

		Connection connection = null;
		Statement stmt = null;
		List<Map<String, String>> jobs = new ArrayList<Map<String, String>>();
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			String query = "select job_name, job_group from QRTZ_TRIGGERS where description='" + name
					+ "' and job_name='" + job + "'";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Map<String, String> data = new HashMap<String, String>();
				data.put("name", rs.getString(1));
				data.put("group", rs.getString(2));
				jobs.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, stmt, connection);
		}
		return jobs;
	}

	public List<Map<String, Object>> getAllScheduledJobs(final String name) {

		Connection connection = null;
		Statement stmt = null;
		List<Map<String, Object>> jobs = new ArrayList<Map<String, Object>>();
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			String query = "select job_group, job_name, next_fire_time, start_time, trigger_name, trigger_state, description from QRTZ_TRIGGERS";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("job_group", rs.getString(1));
				data.put("job_name", rs.getString(2));
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date(Long.valueOf(rs.getString(3))));
				data.put("next_fire_time", sdf.format(cal.getTime()));
				data.put("start_time", sdf.format(cal.getTime()));
				data.put("trigger_name", rs.getString(5));
				data.put("trigger_state", rs.getString(6));
				data.put("desc", rs.getString(7));

				jobs.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, stmt, connection);
		}
		return jobs;
	}

	public List<Map<String, String>> getAllJobs(final String name) {
		List<Map<String, String>> jobs = new ArrayList<Map<String, String>>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			connection = getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(
					"select id, name, owner, grp, active, created, modified, startdate, status from dl_job order by created desc limit 10");
			while (rs.next()) {
				Map<String, String> data = new HashMap<String, String>();
				data.put("id", rs.getString(1));
				data.put("name", rs.getString(2));
				data.put("owner", rs.getString(3));
				data.put("grp", rs.getString(4));
				data.put("active", rs.getString(5));
				data.put("created", rs.getString(6));
				data.put("modified", rs.getString(7));
				data.put("startdate", rs.getString(8));
				data.put("status", rs.getString(9));
				jobs.add(data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(rs, statement, connection);
		}

		return jobs;
	}

	public boolean deleteJob(final String name) {

		Connection connection = null;
		Statement stmt = null;
		try {
			connection = getConnection();
			stmt = connection.createStatement();
			String query = "delete from dl_job where id='" + name + "'";
			logger.info(query);
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(null, stmt, connection);
		}

		return false;
	}
}
