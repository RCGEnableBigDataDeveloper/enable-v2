package com.rcggs.enable.data.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcggs.datalake.connect.DatalakeConnectionFactory;
import com.rcggs.enable.data.service.StudentService;

@Controller
public class IndexController extends AbstractController {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);

	ObjectMapper mapper = new ObjectMapper();

	DatalakeConnectionFactory datalakeConnectionFactory = new DatalakeConnectionFactory();

	@Value("${example.message}")
	private String message;

	@Autowired
	private StudentService studentService;

	@RequestMapping(value = "/message", method = RequestMethod.GET)
	@ResponseBody
	public String getMessage() {
		return message;
	}

	@RequestMapping(value = "/students-ftl", method = RequestMethod.GET)
	public String loadStudentsFtl(Model m) {
		m.addAttribute("students", studentService.findAll());
		return "students-ftl";
	}

	@RequestMapping(value = "/canvas", method = RequestMethod.GET)
	public String loadStudentsJsp(Model m) {
		m.addAttribute("students", studentService.findAll());
		return "canvas";
	}
}
