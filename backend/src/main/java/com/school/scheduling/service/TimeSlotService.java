package com.school.scheduling.service;

import com.school.scheduling.entity.TimeSlot;
import java.util.List;

public interface TimeSlotService {
    List<TimeSlot> getAllTimeSlots();
    TimeSlot getById(Long id);
    List<TimeSlot> getByDayOfWeek(Integer dayOfWeek);
    boolean addTimeSlot(TimeSlot timeSlot);
    boolean updateTimeSlot(TimeSlot timeSlot);
    boolean deleteTimeSlot(Long id);
}
