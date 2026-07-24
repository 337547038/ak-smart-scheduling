package com.school.scheduling.mapper;

import com.school.scheduling.entity.Course;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface CourseMapper {
    List<Course> findAll();
    Course findById(@Param("id") Long id);
    int insert(Course course);
    int update(Course course);
    int deleteById(@Param("id") Long id);
    List<Course> findCoursesByTeacherId(@Param("teacherId") Long teacherId);
}
