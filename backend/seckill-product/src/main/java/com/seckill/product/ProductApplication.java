package com.seckill.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 商品服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.seckill"})
@MapperScan("com.seckill.product.mapper")
public class ProductApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
        System.out.println("========================================");
        System.out.println("商品服务启动成功！");
        System.out.println("========================================");
    }
}

