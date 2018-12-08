package com.rcggs.datalake.connect.fs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.base.Throwables;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;

public class RemoteFileSystemConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	private final Logger logger = Logger.getLogger(getClass());

	ConnectionConfig config;

	public RemoteFileSystemConnection(ConnectionConfig config) {
		super(config);
		try {
			this.config = config;
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public String[] getFiles() {
		JSch jsch = new JSch();

		String host = config.getHost();
		Channel channel = null;
		Session session = null;
		InputStream in = null;
		Appendable buffer = null;
		try {

			buffer = new StringBuilder();
			session = jsch.getSession(config.getUser(), host, Integer.parseInt(config.getPort()));

			java.util.Properties properties = new java.util.Properties();
			properties.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
			properties.put("StrictHostKeyChecking", "no");

			if (config.getProperties() != null && config.getProperties().getProperty("key.file.path") != null
					&& !"".equals(config.getProperties().getProperty("key.file.path"))) {
				jsch.addIdentity(config.getProperties().getProperty("key.file.path"));
			} else {
				session.setPassword(config.getPwd());
			}

			session.setConfig(properties);
			session.connect(10000);
			if (!session.isConnected()) {

			}
			channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand("ls " + config.getPath());
			in = channel.getInputStream();
			((ChannelExec) channel).setErrStream(System.err);
			channel.connect();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					buffer.append(new String(tmp, 0, i));

				}
				if (channel.isClosed()) {
					logger.info("exit-status: " + channel.getExitStatus());
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception ee) {
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
			logger.error(Throwables.getStackTraceAsString(e));
			e.printStackTrace();
		} finally {
			channel.disconnect();
			session.disconnect();
			try {
				in.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}

		return buffer.toString().split("\\r?\\n");
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(final String name) throws Exception {

		final Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);

		List<ItemDefinition> columnDefs = new ArrayList<ItemDefinition>();

		ItemDefinition idef = new ItemDefinition();
		idef.setName(config.getPath());
		idef.setItemType(config.getType());
		idef.setType(config.getType());
		idef.setParent(config.getPath());
		idef.setData(data);

		for (String file : getFiles()) {
			ItemDefinition cd = new ItemDefinition();
			cd.setName(file);
			cd.setData(data);
			cd.setType(config.getType());
			cd.setItemType("remote-file");
			cd.setParent(config.getPath());
			columnDefs.add(cd);
		}

		map.put(idef, columnDefs);

		return map;
	}

}
