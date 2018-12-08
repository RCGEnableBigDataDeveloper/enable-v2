package com.rcggs.enable.data.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

public class TrifactaTemplate {

	final private static Map<String, String> templates = new HashMap<>();

	static {
		try {
			Class<?> clazz = TrifactaTemplate.class;
			String tplDir = "/templates/";
			List<String> files = IOUtils.readLines(
					clazz.getResourceAsStream(tplDir), "UTF-8");
			for (String file : files) {
				InputStream template = clazz.getResourceAsStream(tplDir + file);
				templates.put(file.substring(0, file.lastIndexOf(".")), IOUtils.toString(template, "UTF-8"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String get(final String name) {
		return templates.get(name);
	}
	
	
	public static void main(String[] args) {
		String tpl = templates.get("createFlow");
		System.out.println(templates);
		System.out.println(String.format(tpl, "AAA", "BBB"));
	}

}
