package com.seckill.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 订单服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.seckill"})
@MapperScan("com.seckill.order.mapper")
public class OrderApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
        System.out.println("========================================");
        System.out.println("订单服务启动成功！");
        System.out.println("========================================");
    }
}

