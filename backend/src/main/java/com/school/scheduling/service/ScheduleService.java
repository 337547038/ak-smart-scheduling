package com.school.scheduling.service;

import com.school.scheduling.dto.ScheduleAdjustDTO;
import com.school.scheduling.entity.Schedule;
import com.school.scheduling.entity.ScheduleChange;
import java.util.List;
import java.util.Map;

public interface ScheduleService {
    List<Schedule> getAllSchedules();
    List<Schedule> getSchedulesByClass(Long classId);
    List<Schedule> getSchedulesByTeacher(Long teacherId);
    Schedule getById(Long id);
    
    // 一键排课
    Map<String, Object> autoSchedule();
    
    // 清空排课
    boolean clearSchedules();
    
    // 调整排课
    ScheduleChange adjustSchedule(ScheduleAdjustDTO dto, Long operatorId);
    
    // 获取变更记录
    List<ScheduleChange> getChangeHistory();
}
