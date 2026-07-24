package com.school.scheduling.mapper;

import com.school.scheduling.entity.TimeSlot;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface TimeSlotMapper {
    List<TimeSlot> findAll();
    TimeSlot findById(@Param("id") Long id);
    List<TimeSlot> findByDayOfWeek(@Param("dayOfWeek") Integer dayOfWeek);
    int insert(TimeSlot timeSlot);
    int update(TimeSlot timeSlot);
    int deleteById(@Param("id") Long id);
}
