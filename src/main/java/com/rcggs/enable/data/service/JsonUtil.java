package com.rcggs.enable.data.service;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

	private static final ObjectMapper mapper = new ObjectMapper();

	public static JsonNode parse(final String json) {
		JsonNode jsonNode = null;
		try {
			jsonNode = mapper.readTree(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonNode;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> map(final String json) {
		JsonNode jsonNode = parse(json);
		Map<String, Object> result = mapper.convertValue(jsonNode, Map.class);
		return result;
	}
}
