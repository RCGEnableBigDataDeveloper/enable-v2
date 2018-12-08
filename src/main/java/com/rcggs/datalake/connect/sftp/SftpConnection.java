package com.rcggs.datalake.connect.sftp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;
import com.rcggs.datalake.core.model.SchemaDef;

public class SftpConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	private static final Logger logger = LoggerFactory.getLogger(SftpConnection.class);
	ConnectionConfig config;

	public SftpConnection(ConnectionConfig config) {
		super(config);
		try {
			this.config = config;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(final String name) {
		JSch jsch = new JSch();
		Session session = null;
		Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("config", config);
		try {
			session = jsch.getSession(config.getUser(), config.getHost(), Integer.parseInt(config.getPort()));
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(config.getPwd());
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> list = sftpChannel.ls("*");
			List<ItemDefinition> dirDefs = new ArrayList<ItemDefinition>();

			List<SchemaDef> schemas = null;

			for (ChannelSftp.LsEntry entry : list) {
				if (!entry.getAttrs().isDir()) {
					// sftpChannel.cd(entry.getFilename());
				} else {
					ItemDefinition directory = new ItemDefinition();
					directory.setName(entry.getFilename());
					directory.setData(data);
					directory.setItemType("directory");
					directory.setType(config.getType());
					if (schemas == null) {
						schemas = buildSchema(entry, sftpChannel);
					}
					directory.setSchema(schemas);
					dirDefs.add(directory);
				}
			}
			
			ItemDefinition folder = new ItemDefinition();
			folder.setName(sftpChannel.pwd());
			folder.setItemType("folder");
			map.put(folder, dirDefs);

		} catch (JSchException | SftpException | IOException e) {
			e.printStackTrace();
		} finally {
			session.disconnect();
		}
		return map;
	}

	/**
	 * Build schema from an existing file.
	 * 
	 * @param folder
	 * @param sftpChannel
	 * @return
	 * @throws SftpException
	 * @throws IOException
	 */
	private List<SchemaDef> buildSchema(ChannelSftp.LsEntry folder, ChannelSftp sftpChannel)
			throws SftpException, IOException {

		List<SchemaDef> schemas = null;
		// look for schema
		if (config.getProperties() != null) {
			String schemaLocation = config.getProperties().getProperty("schema.location");
			String schemaType = config.getProperties().getProperty("schema.type");

			if (schemaLocation != null) {
				schemas = new LinkedList<SchemaDef>();
				if ("header.row".equalsIgnoreCase(schemaType)) {
					// read header from file (comman or tab delimited)
					String row = new String(Files.readFirstLine(new File(schemaLocation), Charset.defaultCharset()));
					String[] headers = row.split("\t|,");
					for (String hdr : headers) {
						SchemaDef schema = new SchemaDef();
						schema.setName(hdr);
						schema.setValue("string");
						schema.setChecked(true);
						schemas.add(schema);
					}
				}
				if ("json".equalsIgnoreCase(schemaType)) {
					throw new UnsupportedOperationException("Json schema type");
				}
				if ("xml".equalsIgnoreCase(schemaType)) {
					throw new UnsupportedOperationException("xml schema type");
				}
			}
		}

		if (schemas == null)
			logger.warn("No schema provided...");
		return schemas;
	}
}