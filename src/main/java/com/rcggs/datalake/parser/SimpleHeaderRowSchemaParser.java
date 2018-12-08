package com.rcggs.datalake.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.rcggs.datalake.core.model.SchemaDef;
import com.rcggs.enable.data.resource.ResourceReader;

public class SimpleHeaderRowSchemaParser extends ResourceReader<SchemaDef> implements SchemaParser {

	final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public List<SchemaDef> read(String path, String type, final Map<String, String> pathMap) {

		List<SchemaDef> schema = new LinkedList<SchemaDef>();

		try {

			System.out.println(path);
			System.out.println(pathMap);

			BufferedReader brTest = new BufferedReader(new FileReader(path));
			String text = brTest.readLine();

			for (String x : text.split("\\|")) {
				logger.info(x);
				SchemaDef def = new SchemaDef();
				def.setName(x);
				def.setValue("string");
				schema.add(def);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return schema;
	}

	@Override
	public void parse(String filePath) throws Exception {

	}

	public String[] getFiles(String path) {

		JSch jsch = new JSch();
		String host = "192.168.56.101";
		Channel channel = null;
		Session session = null;
		InputStream in = null;
		Appendable buffer = null;
		try {
			buffer = new StringBuilder();
			session = jsch.getSession("root", host, Integer.parseInt("22"));

			java.util.Properties properties = new java.util.Properties();
			properties.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
			properties.put("StrictHostKeyChecking", "no");

			session.setPassword("adminuser");

			session.setConfig(properties);
			session.connect(10000);
			if (!session.isConnected()) {

			}
			channel = session.openChannel("exec");

			((ChannelExec) channel).setCommand("head -1 " + path);
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
			e.printStackTrace();
		} finally {
			channel.disconnect();
			session.disconnect();
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return buffer.toString().split("\\r?\\n");
	}
}
