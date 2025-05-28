package com.logistics.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.logistics.service.dao.mapper")
@EnableCaching
@EnableAsync
@EnableScheduling
public class LogisticsServiceApplication {

    public static void main(String[] args) {
        // Java 8 兼容的系统属性设置
        System.setProperty("spring.profiles.default", "dev");
        SpringApplication.run(LogisticsServiceApplication.class, args);
        System.out.println("物流分析服务启动成功!");
    }
}