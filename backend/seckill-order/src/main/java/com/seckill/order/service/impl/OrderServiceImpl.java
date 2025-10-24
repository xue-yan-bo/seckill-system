package com.seckill.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.exception.BusinessException;
import com.seckill.model.entity.Order;
import com.seckill.model.entity.Product;
import com.seckill.model.entity.SeckillActivity;
import com.seckill.order.mapper.OrderMapper;
import com.seckill.order.mapper.ProductMapper;
import com.seckill.order.mapper.SeckillActivityMapper;
import com.seckill.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    @Autowired
    private SeckillActivityMapper seckillActivityMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createSeckillOrder(Long userId, Long activityId) {
        // 查询秒杀活动
        SeckillActivity activity = seckillActivityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException("秒杀活动不存在");
        }
        
        // 查询商品信息
        Product product = productMapper.selectById(activity.getProductId());
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        
        // 生成订单号
        String orderNo = generateOrderNo();
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setProductId(product.getId());
        order.setActivityId(activityId);
        order.setProductName(product.getName());
        order.setProductImage(product.getImage());
        order.setQuantity(1);
        order.setPrice(activity.getSeckillPrice());
        order.setTotalAmount(activity.getSeckillPrice());
        order.setStatus(0); // 待支付
        
        this.save(order);
        
        log.info("创建秒杀订单成功 - 订单号: {}, 用户ID: {}, 活动ID: {}", orderNo, userId, activityId);
    }
    
    @Override
    public Page<Order> getOrdersByUserId(Long userId, Integer pageNum, Integer pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getUserId, userId);
        queryWrapper.orderByDesc(Order::getCreateTime);
        
        return this.page(page, queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, Long userId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不允许取消");
        }
        
        order.setStatus(2); // 已取消
        this.updateById(order);
        
        log.info("取消订单成功 - 订单号: {}", order.getOrderNo());
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void payOrder(Long orderId, Long userId) {
        Order order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }
        
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不允许支付");
        }
        
        order.setStatus(1); // 已支付
        order.setPayTime(LocalDateTime.now());
        this.updateById(order);
        
        log.info("支付订单成功 - 订单号: {}", order.getOrderNo());
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        // 格式：时间戳 + 随机数
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timeStr = LocalDateTime.now().format(formatter);
        Random random = new Random();
        int randomNum = random.nextInt(10000);
        return timeStr + String.format("%04d", randomNum);
    }
}

