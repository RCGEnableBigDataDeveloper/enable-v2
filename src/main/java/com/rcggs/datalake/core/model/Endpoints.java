package com.rcggs.datalake.core.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public enum Endpoints implements Serializable {

	AWS("aws-s3"), SFTP("sftp"), HDFS("hdfs2"), SOLR("solr"), FILE("file"), MYSQL(
			"mysql"), ORACLE("oracle");

	public static final Map<String, Endpoints> lookup = new HashMap<String, Endpoints>();

	private final String stringValue;

	private Endpoints(final String s) {
		stringValue = s;
	}

	public String toString() {
		return stringValue;
	}

	static {
		for (Endpoints e : Endpoints.values()) {
			lookup.put(e.toString(), e);
		}
	}

	public static boolean isXls(final String path) {
		return path.endsWith(".xls") || path.endsWith(".xlsx");
	}
}
