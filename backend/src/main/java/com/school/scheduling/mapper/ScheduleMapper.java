package com.school.scheduling.mapper;

import com.school.scheduling.entity.Schedule;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ScheduleMapper {
    List<Schedule> findAll();
    Schedule findById(@Param("id") Long id);
    List<Schedule> findByClassId(@Param("classId") Long classId);
    List<Schedule> findByTeacherId(@Param("teacherId") Long teacherId);
    Schedule findByTimeSlotAndClass(@Param("timeSlotId") Long timeSlotId, @Param("classId") Long classId, @Param("weekNumber") Integer weekNumber);
    Schedule findByTimeSlotAndTeacher(@Param("timeSlotId") Long timeSlotId, @Param("teacherId") Long teacherId, @Param("weekNumber") Integer weekNumber);
    int insert(Schedule schedule);
    int update(Schedule schedule);
    int deleteById(@Param("id") Long id);
    int deleteAll();
}
