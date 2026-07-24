package com.school.scheduling;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.school.scheduling.mapper")
public class SchedulingApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchedulingApplication.class, args);
        System.out.println("========================================");
        System.out.println("  智能排课系统启动成功！");
        System.out.println("  接口地址: http://localhost:8080/api");
        System.out.println("========================================");
    }
}
