package com.rcggs.datalake.connect;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

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
	
	public LoginContext kinit(final String username, final char[] password) throws LoginException {
		LoginContext lc = new LoginContext(getClass().getSimpleName(), new CallbackHandler() {
			public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
				for (Callback c : callbacks) {
					if (c instanceof NameCallback)
						((NameCallback) c).setName(username);
					if (c instanceof PasswordCallback)
						((PasswordCallback) c).setPassword(password);
				}
			}
		});
		lc.login();
		return lc;
	}
}
