package com.seckill.seckill.mq;

import com.alibaba.fastjson2.JSON;
import com.seckill.seckill.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单消息发送者
 */
@Slf4j
@Component
public class OrderMessageSender {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 发送订单消息
     */
    public void sendOrderMessage(Long userId, Long activityId) {
        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("activityId", activityId);
        message.put("timestamp", System.currentTimeMillis());
        
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_ROUTING_KEY,
                JSON.toJSONString(message)
        );
        
        log.info("发送订单消息到MQ - 用户ID: {}, 活动ID: {}", userId, activityId);
    }
}

