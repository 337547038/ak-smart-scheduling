package com.school.scheduling.service.impl;

import com.school.scheduling.entity.Course;
import com.school.scheduling.mapper.CourseMapper;
import com.school.scheduling.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<Course> getAllCourses() {
        return courseMapper.findAll();
    }

    @Override
    public Course getById(Long id) {
        return courseMapper.findById(id);
    }

    @Override
    public boolean addCourse(Course course) {
        return courseMapper.insert(course) > 0;
    }

    @Override
    public boolean updateCourse(Course course) {
        return courseMapper.update(course) > 0;
    }

    @Override
    public boolean deleteCourse(Long id) {
        return courseMapper.deleteById(id) > 0;
    }
}
