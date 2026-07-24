package com.school.scheduling.service.impl;

import com.school.scheduling.entity.TimeSlot;
import com.school.scheduling.mapper.TimeSlotMapper;
import com.school.scheduling.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TimeSlotServiceImpl implements TimeSlotService {

    @Autowired
    private TimeSlotMapper timeSlotMapper;

    @Override
    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotMapper.findAll();
    }

    @Override
    public TimeSlot getById(Long id) {
        return timeSlotMapper.findById(id);
    }

    @Override
    public List<TimeSlot> getByDayOfWeek(Integer dayOfWeek) {
        return timeSlotMapper.findByDayOfWeek(dayOfWeek);
    }

    @Override
    public boolean addTimeSlot(TimeSlot timeSlot) {
        return timeSlotMapper.insert(timeSlot) > 0;
    }

    @Override
    public boolean updateTimeSlot(TimeSlot timeSlot) {
        return timeSlotMapper.update(timeSlot) > 0;
    }

    @Override
    public boolean deleteTimeSlot(Long id) {
        return timeSlotMapper.deleteById(id) > 0;
    }
}
