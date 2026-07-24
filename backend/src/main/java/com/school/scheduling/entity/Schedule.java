package com.school.scheduling.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Schedule {
    private Long id;
    private Long classId;
    private String className;
    private Long courseId;
    private String courseName;
    private Long teacherId;
    private String teacherName;
    private Long timeSlotId;
    private String slotName;
    private Integer dayOfWeek;
    private String timeRange;
    private Integer weekNumber;
    private String classroom;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
