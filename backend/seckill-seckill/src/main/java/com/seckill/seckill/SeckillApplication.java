package com.seckill.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 秒杀服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.seckill"})
@MapperScan("com.seckill.seckill.mapper")
public class SeckillApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SeckillApplication.class, args);
        System.out.println("========================================");
        System.out.println("秒杀服务启动成功！");
        System.out.println("========================================");
    }
}

