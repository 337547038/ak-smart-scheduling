package com.school.scheduling.dto;

import lombok.Data;

@Data
public class ScheduleAdjustDTO {
    private Long scheduleId;
    private Long newTimeSlotId;
    private Long newTeacherId;
    private String classroom;
    private String changeType;
    private String changeReason;
}
