package com.school.scheduling.controller;

import com.school.scheduling.common.Result;
import com.school.scheduling.dto.ScheduleAdjustDTO;
import com.school.scheduling.entity.Schedule;
import com.school.scheduling.entity.ScheduleChange;
import com.school.scheduling.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
@CrossOrigin
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/list")
    public Result<List<Schedule>> list(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long teacherId) {
        if (classId != null) {
            return Result.success(scheduleService.getSchedulesByClass(classId));
        }
        if (teacherId != null) {
            return Result.success(scheduleService.getSchedulesByTeacher(teacherId));
        }
        return Result.success(scheduleService.getAllSchedules());
    }

    @GetMapping("/{id}")
    public Result<Schedule> getById(@PathVariable Long id) {
        return Result.success(scheduleService.getById(id));
    }

    @PostMapping("/auto")
    public Result<Map<String, Object>> autoSchedule() {
        try {
            Map<String, Object> result = scheduleService.autoSchedule();
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/clear")
    public Result<?> clear() {
        try {
            return scheduleService.clearSchedules() ? Result.success() : Result.error("清空失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/adjust")
    public Result<ScheduleChange> adjust(@RequestBody ScheduleAdjustDTO dto, HttpServletRequest request) {
        try {
            Long operatorId = (Long) request.getAttribute("userId");
            ScheduleChange change = scheduleService.adjustSchedule(dto, operatorId);
            return Result.success(change);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/changes")
    public Result<List<ScheduleChange>> changes() {
        return Result.success(scheduleService.getChangeHistory());
    }
}
