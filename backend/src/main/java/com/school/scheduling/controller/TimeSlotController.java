package com.school.scheduling.controller;

import com.school.scheduling.common.Result;
import com.school.scheduling.entity.Course;
import com.school.scheduling.entity.TimeSlot;
import com.school.scheduling.service.CourseService;
import com.school.scheduling.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/timeslot")
@CrossOrigin
public class TimeSlotController {

    @Autowired
    private TimeSlotService timeSlotService;

    @GetMapping("/list")
    public Result<List<TimeSlot>> list() {
        return Result.success(timeSlotService.getAllTimeSlots());
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody TimeSlot timeSlot) {
        try {
            return timeSlotService.addTimeSlot(timeSlot) ? Result.success() : Result.error("添加失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody TimeSlot timeSlot) {
        try {
            return timeSlotService.updateTimeSlot(timeSlot) ? Result.success() : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            return timeSlotService.deleteTimeSlot(id) ? Result.success() : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

@RestController
@RequestMapping("/course")
@CrossOrigin
class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public Result<List<Course>> list() {
        return Result.success(courseService.getAllCourses());
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody Course course) {
        try {
            return courseService.addCourse(course) ? Result.success() : Result.error("添加失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody Course course) {
        try {
            return courseService.updateCourse(course) ? Result.success() : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            return courseService.deleteCourse(id) ? Result.success() : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
