package com.rcggs.datalake.dao;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

public class WorkflowDao extends AbstractDao {

	final Logger logger = LoggerFactory.getLogger(getClass());

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

	Map<String, String> getData(final String query, final String... name) {
		Connection conn = null;
		PreparedStatement preparedStmt = null;
		ResultSet rs = null;
		Map<String, String> data = new HashMap<String, String>();
		try {
			conn = getConnection();
			preparedStmt = conn.prepareStatement(query);
			int pos = 1;

			for (int i = 0; i < name.length; i++) {
				preparedStmt.setString(pos, name[i]);
				pos++;
			}

			rs = preparedStmt.executeQuery();
			while (rs.next()) {
				data.put("name", rs.getString(1));
				data.put("owner", rs.getString(2));
				data.put("layout", rs.getString(3));
				data.put("model", rs.getString(4));
			}
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs, preparedStmt, conn);
		}
		return null;
	}

	public Map<String, String> openWorkflow(final String parent, final String name) {
		String query = "select name, owner, layout, model from dl_wf_layout where parent_fldr_id=? and name=?";
		return getData(query, parent, name);
	}

	public int updateWorkflow(String name) {
		String query = "update dl_wf_layout set modified = ?, layout= ?, model = ? where name = ?";
		Connection conn = null;
		PreparedStatement preparedStmt = null;
		try {
			conn = getConnection();
			name = URLDecoder.decode(name, "UTF-8");
			JsonNode data = mapper.readTree(name);
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setDate(1, new Date(System.currentTimeMillis()));
			preparedStmt.setString(2, data.get("data").getTextValue());
			preparedStmt.setString(3, data.get("model").getTextValue());
			preparedStmt.setString(4, data.get("filename").getTextValue());
			return preparedStmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(null, preparedStmt, conn);
		}

		return 0;
	}

	public int saveWorkflowFolder(String name) {
		Connection conn = null;
		PreparedStatement preparedStmt = null;
		try {
			conn = getConnection();
			String query = "update dl_wf_layout_fldr set name=?";
			preparedStmt = conn.prepareStatement(query);
			// preparedStmt.setString(1, UUID.randomUUID().toString());
			preparedStmt.setString(1, name);
			// preparedStmt.setString(3, "1");

			return preparedStmt.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(null, preparedStmt, conn);
		}

		return 0;
	}

	public int saveWorkflow(String name) {

		Connection conn = null;
		PreparedStatement preparedStmt = null;
		try {

			conn = getConnection();
			name = name.replaceAll("%", "");
			name = URLDecoder.decode(name, "UTF-8");
			JsonNode data = mapper.readTree(name);

			Map<String, String> result = getData(
					"select id, layout, model from dl_wf_layout where name = ? and parent_fldr_id='"
							+ data.get("parent_fldr_id").getTextValue() + "'",
					data.get("filename").getTextValue());

			if (result.size() > 0) {
				return 0;
			}

			String query = "insert into dl_wf_layout(id, name, owner, grp, active, created, modified, parent_fldr_id, layout, model)values(?,?,?,?,?,?,?,?,?,?)";
			preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, UUID.randomUUID().toString());
			preparedStmt.setString(2, data.get("filename").getTextValue());
			preparedStmt.setString(3, data.get("parent_fldr_id").getTextValue());
			preparedStmt.setString(4, data.get("grp").getTextValue());
			preparedStmt.setBoolean(5, true);
			preparedStmt.setDate(6, new Date(System.currentTimeMillis()));
			preparedStmt.setDate(7, new Date(System.currentTimeMillis()));
			preparedStmt.setString(8, data.get("parent_fldr_id").getTextValue());
			preparedStmt.setString(9, data.get("layout").toString());
			preparedStmt.setString(10, (data.get("model") != null) ? data.get("model").toString() : "");

			return preparedStmt.executeUpdate();

		} catch (Exception e) {
			logger.info(Throwables.getStackTraceAsString(e));
			e.printStackTrace();
		} finally {
			close(null, preparedStmt, conn);
		}

		return -1;
	}

	public String buildLevels() {

		Connection conn = null;
		ResultSet rs = null;
		Statement st = null;

		try {

			String query = "select name from dl_wf_layout_fldr";
			conn = getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(query);
			rs.next();
			return rs.getString(1);

			// String parentQry = "select * from dl_wf_layout_fldr order by
			// seq";
			// String childQry = "select * from dl_wf_layout where
			// parent_fldr_id=";
			// LevelBuilder v = new LevelBuilder();
			// return (v.build(parentQry, childQry));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs, st, conn);
		}
		return null;
	}

	public List<Map<String, String>> getSavedFiles() {
		List<Map<String, String>> files = new ArrayList<>();

		Connection conn = null;
		ResultSet rs = null;
		Statement st = null;

		try {

			String query = "select  id, name, parent_fldr_id, layout, model from dl_wf_layout";
			conn = getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				Map<String, String> file = new HashMap<>();
				file.put("id", rs.getString(1));
				file.put("name", rs.getString(2));
				file.put("parent_fldr_id", rs.getString(3));
				file.put("layout", rs.getString(4));
				file.put("model", rs.getString(5));
				files.add(file);
			}

			return files;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs, st, conn);
		}
		return null;
	}
}
