package com.school.scheduling.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ClassInfo {
    private Long id;
    private String className;
    private String grade;
    private Integer studentCount;
    private Long headTeacherId;
    private String headTeacherName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
