package com.rcggs.enable.data.service;

import java.util.List;

import com.fernandospr.example.exceptions.ResourceAlreadyExistsException;
import com.fernandospr.example.model.Course;

public interface CourseService {
	List<Course> findAll();

	Course find(Integer id);
	
	boolean exists(Integer id);
	
	Course save(Course course) throws ResourceAlreadyExistsException;
	
	Course update(Integer id, Course newCourse);
	
	void delete(Course course);
}
