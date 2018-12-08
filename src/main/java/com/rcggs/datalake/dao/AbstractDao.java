package com.rcggs.datalake.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.rcggs.datalake.pool.DataSource;

public abstract class AbstractDao {

	Connection getConnection() throws SQLException {
		return DataSource.getConnection();
	}

	void close(ResultSet rs, Statement statement, Connection connection) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
