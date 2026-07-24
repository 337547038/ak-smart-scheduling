-- 智能排课系统数据库脚本
CREATE DATABASE IF NOT EXISTS smart_scheduling DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_scheduling;

-- 用户表（管理员和老师）
CREATE TABLE `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
    `role` VARCHAR(20) NOT NULL COMMENT '角色：ADMIN-管理员, TEACHER-老师',
    `phone` VARCHAR(20) COMMENT '联系电话',
    `email` VARCHAR(100) COMMENT '邮箱',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 班级表
CREATE TABLE `class` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '班级ID',
    `class_name` VARCHAR(50) NOT NULL COMMENT '班级名称',
    `grade` VARCHAR(20) NOT NULL COMMENT '年级',
    `student_count` INT DEFAULT 0 COMMENT '学生人数',
    `head_teacher_id` BIGINT COMMENT '班主任ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`head_teacher_id`) REFERENCES `user`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班级表';

-- 课程表
CREATE TABLE `course` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程ID',
    `course_name` VARCHAR(50) NOT NULL COMMENT '课程名称',
    `course_code` VARCHAR(20) UNIQUE COMMENT '课程编码',
    `weekly_hours` INT NOT NULL DEFAULT 1 COMMENT '每周课时数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程表';

-- 时间段配置（课时管理）
CREATE TABLE `time_slot` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '时间段ID',
    `slot_name` VARCHAR(20) NOT NULL COMMENT '节次名称，如第1节',
    `day_of_week` INT NOT NULL COMMENT '星期几：1-7',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_day_slot` (`day_of_week`, `slot_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间段配置表';

-- 排课表
CREATE TABLE `schedule` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '排课ID',
    `class_id` BIGINT NOT NULL COMMENT '班级ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    `teacher_id` BIGINT NOT NULL COMMENT '任课老师ID',
    `time_slot_id` BIGINT NOT NULL COMMENT '时间段ID',
    `week_number` INT DEFAULT 1 COMMENT '第几周',
    `classroom` VARCHAR(50) COMMENT '教室',
    `status` VARCHAR(20) DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常, ADJUSTED-已调课, CANCELLED-已取消',
    `remark` VARCHAR(200) COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`class_id`) REFERENCES `class`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`time_slot_id`) REFERENCES `time_slot`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排课表';

-- 排课变更记录表
CREATE TABLE `schedule_change` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '变更ID',
    `schedule_id` BIGINT NOT NULL COMMENT '原排课ID',
    `old_time_slot_id` BIGINT NOT NULL COMMENT '原时间段ID',
    `new_time_slot_id` BIGINT COMMENT '新时间段ID（调课）',
    `new_teacher_id` BIGINT COMMENT '新老师ID（换老师）',
    `change_type` VARCHAR(20) NOT NULL COMMENT '变更类型：ADJUST_TIME-调课, REPLACE_TEACHER-换老师, CANCEL-取消',
    `change_reason` VARCHAR(200) COMMENT '变更原因',
    `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`schedule_id`) REFERENCES `schedule`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`operator_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排课变更记录表';

-- 教师课程关联表（教师可教授的课程）
CREATE TABLE `teacher_course` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `teacher_id` BIGINT NOT NULL COMMENT '教师ID',
    `course_id` BIGINT NOT NULL COMMENT '课程ID',
    FOREIGN KEY (`teacher_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`course_id`) REFERENCES `course`(`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_teacher_course` (`teacher_id`, `course_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师课程关联表';

-- 插入初始管理员账户（密码：admin123）
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `phone`, `email`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt7iAt6', '系统管理员', 'ADMIN', '13800000000', 'admin@school.edu.cn'),
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt7iAt6', '张老师', 'TEACHER', '13800000001', 'zhang@school.edu.cn'),
('teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt7iAt6', '李老师', 'TEACHER', '13800000002', 'li@school.edu.cn'),
('teacher3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt7iAt6', '王老师', 'TEACHER', '13800000003', 'wang@school.edu.cn');

-- 插入初始时间段（一天8节课，周一到周五）
INSERT INTO `time_slot` (`slot_name`, `day_of_week`, `start_time`, `end_time`) VALUES
('第1节', 1, '08:00:00', '08:45:00'),
('第2节', 1, '08:55:00', '09:40:00'),
('第3节', 1, '10:00:00', '10:45:00'),
('第4节', 1, '10:55:00', '11:40:00'),
('第5节', 1, '14:00:00', '14:45:00'),
('第6节', 1, '14:55:00', '15:40:00'),
('第7节', 1, '16:00:00', '16:45:00'),
('第8节', 1, '16:55:00', '17:40:00'),
('第1节', 2, '08:00:00', '08:45:00'),
('第2节', 2, '08:55:00', '09:40:00'),
('第3节', 2, '10:00:00', '10:45:00'),
('第4节', 2, '10:55:00', '11:40:00'),
('第5节', 2, '14:00:00', '14:45:00'),
('第6节', 2, '14:55:00', '15:40:00'),
('第7节', 2, '16:00:00', '16:45:00'),
('第8节', 2, '16:55:00', '17:40:00'),
('第1节', 3, '08:00:00', '08:45:00'),
('第2节', 3, '08:55:00', '09:40:00'),
('第3节', 3, '10:00:00', '10:45:00'),
('第4节', 3, '10:55:00', '11:40:00'),
('第5节', 3, '14:00:00', '14:45:00'),
('第6节', 3, '14:55:00', '15:40:00'),
('第7节', 3, '16:00:00', '16:45:00'),
('第8节', 3, '16:55:00', '17:40:00'),
('第1节', 4, '08:00:00', '08:45:00'),
('第2节', 4, '08:55:00', '09:40:00'),
('第3节', 4, '10:00:00', '10:45:00'),
('第4节', 4, '10:55:00', '11:40:00'),
('第5节', 4, '14:00:00', '14:45:00'),
('第6节', 4, '14:55:00', '15:40:00'),
('第7节', 4, '16:00:00', '16:45:00'),
('第8节', 4, '16:55:00', '17:40:00'),
('第1节', 5, '08:00:00', '08:45:00'),
('第2节', 5, '08:55:00', '09:40:00'),
('第3节', 5, '10:00:00', '10:45:00'),
('第4节', 5, '10:55:00', '11:40:00'),
('第5节', 5, '14:00:00', '14:45:00'),
('第6节', 5, '14:55:00', '15:40:00'),
('第7节', 5, '16:00:00', '16:45:00'),
('第8节', 5, '16:55:00', '17:40:00');

-- 插入示例课程
INSERT INTO `course` (`course_name`, `course_code`, `weekly_hours`) VALUES
('语文', 'YW001', 6),
('数学', 'SX001', 6),
('英语', 'YY001', 5),
('物理', 'WL001', 4),
('化学', 'HX001', 3),
('生物', 'SW001', 3),
('历史', 'LS001', 2),
('地理', 'DL001', 2),
('政治', 'ZZ001', 2),
('体育', 'TY001', 2);

-- 插入示例班级
INSERT INTO `class` (`class_name`, `grade`, `student_count`) VALUES
('高一(1)班', '高一', 45),
('高一(2)班', '高一', 46),
('高二(1)班', '高二', 48),
('高二(2)班', '高二', 47);

-- 教师课程关联
INSERT INTO `teacher_course` (`teacher_id`, `course_id`) VALUES
(2, 1), (2, 2),
(3, 3), (3, 4),
(4, 5), (4, 6);
