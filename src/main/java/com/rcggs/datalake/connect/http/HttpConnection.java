package com.rcggs.datalake.connect.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rcggs.datalake.connect.AbstractDataLakeConnection;
import com.rcggs.datalake.connect.DataLakeConnection;
import com.rcggs.datalake.core.model.ConnectionConfig;
import com.rcggs.datalake.core.model.ItemDefinition;

public class HttpConnection extends AbstractDataLakeConnection implements DataLakeConnection {

	ConnectionConfig config;

	public HttpConnection(ConnectionConfig config) {
		super(config);
		try {
			this.config = config;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<ItemDefinition, List<ItemDefinition>> describe(String name) throws Exception {
		BufferedReader br = null;
		BufferedReader br1 = null;
		final Map<ItemDefinition, List<ItemDefinition>> map = new HashMap<ItemDefinition, List<ItemDefinition>>();
		try {
			URLConnection conn = new URL("https://archive.apache.org/dist/tomcat/").openConnection();
			List<ItemDefinition> columnDefs = new ArrayList<ItemDefinition>();
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("config", config);
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {

				if (inputLine.indexOf("[DIR]") != -1) {

					Pattern titleFinder = Pattern.compile("<a[^>]*>(.*?)</a>",
							Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
					Matcher regexMatcher = titleFinder.matcher(inputLine);
					while (regexMatcher.find()) {
						// System.err.println(regexMatcher.group(1));
						String path = regexMatcher.group(1);
						ItemDefinition idef = new ItemDefinition();
						idef.setName(path);
						idef.setItemType("directory");
						idef.setType(config.getType());
						idef.setData(data);
						columnDefs.add(idef);

						URLConnection conn1 = new URL("https://archive.apache.org/dist/tomcat/" + path)
								.openConnection();
						br1 = new BufferedReader(new InputStreamReader(conn1.getInputStream()));
						String inputLine1;
						List<ItemDefinition> children = new ArrayList<ItemDefinition>();
						while ((inputLine1 = br1.readLine()) != null) {
							if (inputLine1.indexOf("[   ]") != -1 || inputLine1.indexOf("[TXT]") != -1) {
								Matcher regexMatcher1 = titleFinder.matcher(inputLine1);
								System.err.println(inputLine1);
								while (regexMatcher1.find()) {
									ItemDefinition idef1 = new ItemDefinition();
									idef1.setName(regexMatcher1.group(1));
									idef1.setItemType("file");
									idef1.setType(config.getType());
									idef1.setData(data);
									children.add(idef1);
								}
							}

							idef.setChildren(children);
						}
					}
				}
			}

			ItemDefinition idef = new ItemDefinition();
			idef.setName(config.getHost());
			idef.setItemType("directory");
			idef.setType(config.getType());
			idef.setData(data);

			map.put(idef, columnDefs);

			return map;

		} finally {
			br.close();
			br1.close();
		}
	}
}
