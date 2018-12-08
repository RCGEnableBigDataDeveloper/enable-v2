package com.rcggs.enable.data.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rcggs.datalake.connect.DatalakeConnectionFactory;
import com.rcggs.datalake.logging.Log4jMemoryAppender;

public class AbstractController {

	DatalakeConnectionFactory datalakeConnectionFactory;

	protected final static ObjectMapper mapper = new ObjectMapper();
	
	@SuppressWarnings("deprecation")
	protected final static JsonFactory factory = mapper.getJsonFactory();

	protected final static Log4jMemoryAppender log4jMemoryAppender = new Log4jMemoryAppender();

	protected final static ObjectWriter ow = new ObjectMapper().writer();
	
	protected static final String EMPTY_JSON = "{}";

}
