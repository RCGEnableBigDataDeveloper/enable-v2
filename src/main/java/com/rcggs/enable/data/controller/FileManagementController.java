package com.rcggs.enable.data.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rcggs.datalake.configuration.DatalakeContext;


@RestController
public class FileManagementController extends AbstractController {

	final Logger logger = Logger.getLogger(getClass());

	@RequestMapping(value = "/saveFolder/{name}", method = RequestMethod.GET)
	public int saveFolder(final @PathVariable String name) {
		return DatalakeContext.getWorkflowDao().saveWorkflowFolder(name);
	}

	@RequestMapping(value = "/save/{name}", method = RequestMethod.POST)
	public int save(final @RequestBody Object name) {
		logger.info(name.toString().replaceAll("=", ":"));
		return DatalakeContext.getWorkflowDao().saveWorkflow(name.toString().replaceAll("=", ":"));
	}

	@RequestMapping(value = "/update/{name}", method = RequestMethod.POST)
	public int update(final @RequestBody String name) {
		return DatalakeContext.getWorkflowDao().updateWorkflow(name);
	}

	@RequestMapping(value = "/getSavedLayouts/{name}", method = RequestMethod.GET)
	public String getSavedLayouts(final @PathVariable String name) {
		return DatalakeContext.getWorkflowDao().buildLevels();
	}

	@RequestMapping(value = "/getSavedFiles/{name}", method = RequestMethod.GET)
	public String getSavedFiles(final @PathVariable String name) {
		try {
			return mapper.writeValueAsString(DatalakeContext.getWorkflowDao().getSavedFiles());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return name;
	}

	@RequestMapping(value = "/open/{parent}/{name}", method = RequestMethod.GET)
	public Map<String, String> open(final @PathVariable String parent, final @PathVariable String name) {		
		return DatalakeContext.getWorkflowDao().openWorkflow(parent, name);
	}

	@RequestMapping(value = "/getoptions/{name}", method = RequestMethod.GET)
	public String getOptions(final @PathVariable String name) {
		List<Map<String, String>> data = DatalakeContext.getMetadataDao().getOptions(name);
		try {
			return ow.writeValueAsString(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
