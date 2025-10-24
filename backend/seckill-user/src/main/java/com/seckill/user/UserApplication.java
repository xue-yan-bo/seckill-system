package com.seckill.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.seckill"})
@MapperScan("com.seckill.user.mapper")
public class UserApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        System.out.println("========================================");
        System.out.println("用户服务启动成功！");
        System.out.println("========================================");
    }
}

