package com.seckill.order.mq;

import com.alibaba.fastjson2.JSON;
import com.rabbitmq.client.Channel;
import com.seckill.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 订单消息监听器
 */
@Slf4j
@Component
public class OrderMessageListener {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 监听订单消息
     */
    @RabbitListener(queues = "seckill.order.queue")
    public void handleOrderMessage(String messageStr, Message message, Channel channel) {
        try {
            log.info("接收到订单消息: {}", messageStr);
            
            // 解析消息
            @SuppressWarnings("unchecked")
            Map<String, Object> messageMap = JSON.parseObject(messageStr, Map.class);
            Long userId = Long.parseLong(messageMap.get("userId").toString());
            Long activityId = Long.parseLong(messageMap.get("activityId").toString());
            
            // 创建订单
            orderService.createSeckillOrder(userId, activityId);
            
            // 手动确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            
            log.info("订单消息处理成功 - 用户ID: {}, 活动ID: {}", userId, activityId);
            
        } catch (Exception e) {
            log.error("订单消息处理失败: {}", e.getMessage(), e);
            try {
                // 拒绝消息并重新入队
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (Exception ex) {
                log.error("消息拒绝失败", ex);
            }
        }
    }
}

