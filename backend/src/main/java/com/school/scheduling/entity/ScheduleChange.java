package com.school.scheduling.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScheduleChange {
    private Long id;
    private Long scheduleId;
    private Long oldTimeSlotId;
    private String oldTimeSlotInfo;
    private Long newTimeSlotId;
    private String newTimeSlotInfo;
    private Long newTeacherId;
    private String newTeacherName;
    private String changeType;
    private String changeReason;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime createTime;
}
