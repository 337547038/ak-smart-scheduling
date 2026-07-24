package com.school.scheduling.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TeacherCourseMapper {
    List<Long> findCourseIdsByTeacherId(@Param("teacherId") Long teacherId);
    List<Long> findTeacherIdsByCourseId(@Param("courseId") Long courseId);
    int insert(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);
    int delete(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);
    int deleteByTeacherId(@Param("teacherId") Long teacherId);
}
