package com.seckill.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.model.entity.Order;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {
    
    /**
     * 创建秒杀订单
     */
    void createSeckillOrder(Long userId, Long activityId);
    
    /**
     * 根据用户ID分页查询订单
     */
    Page<Order> getOrdersByUserId(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 取消订单
     */
    void cancelOrder(Long orderId, Long userId);
    
    /**
     * 支付订单
     */
    void payOrder(Long orderId, Long userId);
}

