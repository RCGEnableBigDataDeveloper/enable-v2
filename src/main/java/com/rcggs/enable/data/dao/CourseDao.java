package com.rcggs.enable.data.dao;

import java.util.List;

import com.fernandospr.example.model.Course;

public interface CourseDao {

	List<Course> findAll();
	
	Course find(Integer id);
	
	Course save(Course course);

	Course update(Integer id, Course newCourse);

	void delete(Course student);
	
	void deleteById(Integer id);
}
