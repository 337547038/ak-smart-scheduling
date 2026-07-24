package com.school.scheduling.mapper;

import com.school.scheduling.entity.ScheduleChange;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ScheduleChangeMapper {
    List<ScheduleChange> findAll();
    List<ScheduleChange> findByScheduleId(@Param("scheduleId") Long scheduleId);
    int insert(ScheduleChange change);
}
