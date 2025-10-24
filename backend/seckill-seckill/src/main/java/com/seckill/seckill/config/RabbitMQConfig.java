package com.seckill.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 */
@Configuration
public class RabbitMQConfig {
    
    // 订单队列
    public static final String ORDER_QUEUE = "seckill.order.queue";
    
    // 订单交换机
    public static final String ORDER_EXCHANGE = "seckill.order.exchange";
    
    // 订单路由键
    public static final String ORDER_ROUTING_KEY = "seckill.order.routing.key";
    
    /**
     * 创建订单队列
     */
    @Bean
    public Queue orderQueue() {
        return QueueBuilder.durable(ORDER_QUEUE).build();
    }
    
    /**
     * 创建订单交换机
     */
    @Bean
    public DirectExchange orderExchange() {
        return ExchangeBuilder.directExchange(ORDER_EXCHANGE).durable(true).build();
    }
    
    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(ORDER_ROUTING_KEY);
    }
}

