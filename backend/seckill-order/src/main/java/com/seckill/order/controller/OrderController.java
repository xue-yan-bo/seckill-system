package com.seckill.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.common.annotation.SystemLog;
import com.seckill.common.result.Result;
import com.seckill.common.utils.JwtUtil;
import com.seckill.model.entity.Order;
import com.seckill.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 获取用户订单列表
     */
    @GetMapping("/list")
    @SystemLog(value = "查询订单列表", businessType = 0)
    public Result<Page<Order>> getOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader("Authorization") String token) {
        
        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未登录或Token无效");
        }
        
        Page<Order> page = orderService.getOrdersByUserId(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    /**
     * 根据ID查询订单详情
     */
    @GetMapping("/{id}")
    @SystemLog(value = "查询订单详情", businessType = 0)
    public Result<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getById(id);
        return Result.success(order);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel/{id}")
    @SystemLog(value = "取消订单", businessType = 2)
    public Result<?> cancelOrder(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未登录或Token无效");
        }
        
        orderService.cancelOrder(id, userId);
        return Result.success("订单取消成功");
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/pay/{id}")
    @SystemLog(value = "支付订单", businessType = 2)
    public Result<?> payOrder(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        
        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未登录或Token无效");
        }
        
        orderService.payOrder(id, userId);
        return Result.success("支付成功");
    }
}

