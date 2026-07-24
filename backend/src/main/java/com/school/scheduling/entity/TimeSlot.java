package com.school.scheduling.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class TimeSlot {
    private Long id;
    private String slotName;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDateTime createTime;
}
