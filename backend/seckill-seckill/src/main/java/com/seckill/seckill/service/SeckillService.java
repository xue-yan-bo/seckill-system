package com.seckill.seckill.service;

import com.seckill.model.entity.SeckillActivity;

import java.util.List;

/**
 * 秒杀服务接口
 */
public interface SeckillService {
    
    /**
     * 获取秒杀活动列表
     */
    List<SeckillActivity> getSeckillActivityList();
    
    /**
     * 获取秒杀活动详情
     */
    SeckillActivity getSeckillActivityById(Long activityId);
    
    /**
     * 执行秒杀（使用分布式锁）
     */
    void doSeckill(Long userId, Long activityId);
    
    /**
     * 预加载秒杀活动到Redis
     */
    void preloadSeckillActivity();
    
    /**
     * 检查用户是否已参与秒杀
     */
    boolean checkUserSeckill(Long userId, Long activityId);
}

