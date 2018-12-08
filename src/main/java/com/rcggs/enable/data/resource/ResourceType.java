package com.rcggs.enable.data.resource;

public enum ResourceType {

	FILE("file://"), HTTP("http://"), HTTPS("https://"), CLASSPATH("classpath://"), HDFS("hdfs://");
	
	final String text;

	private ResourceType(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}