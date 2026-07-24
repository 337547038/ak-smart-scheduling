package com.school.scheduling.service;

import com.school.scheduling.entity.Course;
import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    Course getById(Long id);
    boolean addCourse(Course course);
    boolean updateCourse(Course course);
    boolean deleteCourse(Long id);
}
