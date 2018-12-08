package com.rcggs.datalake.resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;

import com.google.common.base.CharMatcher;

public abstract class ResourceReader<T> {

	protected static CharMatcher matcher;

	protected static ObjectMapper mapper = new ObjectMapper();

	protected ResourceReader() {
		matcher = CharMatcher.inRange('a', 'z').or(CharMatcher.inRange('A', 'Z'))
				.or(CharMatcher.inRange('0', '9').or(CharMatcher.is('_'))).precomputed();
	}

	public abstract List<T> read(final String path, final String type, final Map<String, String> pathMap);

	protected String clean(final Object string) {
		String value = String.valueOf(string).replaceAll(" ", "_");
		return matcher.retainFrom(value);
	}

	protected InputStream loadResource(final String path, final String type) {
		InputStream is = null;
		try {
			if (type.equals(ResourceType.FILE.name())) {
				File file = new File(path);
				is = new FileInputStream(file);
			} else if (type.equals(ResourceType.CLASSPATH.name())) {
				is = getClass().getResourceAsStream(path);
			} else if (type.equals(ResourceType.HTTP.name()) || type.startsWith(ResourceType.HTTPS.name())) {
				URL url = new URL(path);
				is = new BufferedInputStream(url.openStream());
			} else if (type.startsWith(ResourceType.HDFS.name())) {
				// FileSystem fileSystem = FileSystem.get(new Configuration());
				// is = fileSystem.open(new Path(path));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return is;
	}
}
