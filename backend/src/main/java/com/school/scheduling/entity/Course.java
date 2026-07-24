package com.school.scheduling.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Course {
    private Long id;
    private String courseName;
    private String courseCode;
    private Integer weeklyHours;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
