package com.seckill.seckill.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.utils.RedisUtil;
import com.seckill.model.entity.SeckillActivity;
import com.seckill.seckill.mapper.SeckillActivityMapper;
import com.seckill.seckill.mq.OrderMessageSender;
import com.seckill.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀服务实现类
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {
    
    @Autowired
    private SeckillActivityMapper seckillActivityMapper;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private RedissonClient redissonClient;
    
    @Autowired
    private OrderMessageSender orderMessageSender;
    
    private static final String SECKILL_ACTIVITY_CACHE_KEY = "seckill:activity:";
    private static final String SECKILL_STOCK_KEY = "seckill:stock:";
    private static final String SECKILL_USER_KEY = "seckill:user:";
    private static final String SECKILL_LOCK_KEY = "seckill:lock:";
    
    /**
     * 应用启动时预加载秒杀活动
     */
    @PostConstruct
    public void init() {
        log.info("开始预加载秒杀活动到Redis...");
        preloadSeckillActivity();
        log.info("秒杀活动预加载完成！");
    }
    
    @Override
    public List<SeckillActivity> getSeckillActivityList() {
        LambdaQueryWrapper<SeckillActivity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeckillActivity::getStatus, 1);
        queryWrapper.le(SeckillActivity::getStartTime, LocalDateTime.now());
        queryWrapper.ge(SeckillActivity::getEndTime, LocalDateTime.now());
        queryWrapper.orderByDesc(SeckillActivity::getCreateTime);
        
        return seckillActivityMapper.selectList(queryWrapper);
    }
    
    @Override
    public SeckillActivity getSeckillActivityById(Long activityId) {
        // 先从缓存获取
        String cacheKey = SECKILL_ACTIVITY_CACHE_KEY + activityId;
        Object cacheData = redisUtil.get(cacheKey);
        
        if (cacheData != null) {
            return JSON.parseObject(cacheData.toString(), SeckillActivity.class);
        }
        
        // 缓存未命中，从数据库查询
        SeckillActivity activity = seckillActivityMapper.selectById(activityId);
        if (activity != null) {
            redisUtil.set(cacheKey, JSON.toJSONString(activity), 30, TimeUnit.MINUTES);
        }
        
        return activity;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doSeckill(Long userId, Long activityId) {
        // 获取分布式锁
        String lockKey = SECKILL_LOCK_KEY + activityId + ":" + userId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // 尝试获取锁，等待时间5秒，锁自动释放时间10秒
            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new BusinessException("系统繁忙，请稍后重试");
            }
            
            // 检查用户是否已参与秒杀
            if (checkUserSeckill(userId, activityId)) {
                throw new BusinessException("您已参与过该秒杀活动");
            }
            
            // 从Redis检查库存
            String stockKey = SECKILL_STOCK_KEY + activityId;
            Object stockObj = redisUtil.get(stockKey);
            
            if (stockObj == null) {
                // 缓存未命中，重新加载
                SeckillActivity activity = getSeckillActivityById(activityId);
                if (activity == null) {
                    throw new BusinessException("秒杀活动不存在");
                }
                redisUtil.set(stockKey, activity.getSeckillStock(), 30, TimeUnit.MINUTES);
                stockObj = activity.getSeckillStock();
            }
            
            Integer stock = Integer.parseInt(stockObj.toString());
            if (stock <= 0) {
                throw new BusinessException("商品已售罄");
            }
            
            // 检查活动状态
            SeckillActivity activity = getSeckillActivityById(activityId);
            if (activity == null) {
                throw new BusinessException("秒杀活动不存在");
            }
            
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(activity.getStartTime())) {
                throw new BusinessException("秒杀活动尚未开始");
            }
            if (now.isAfter(activity.getEndTime())) {
                throw new BusinessException("秒杀活动已结束");
            }
            if (activity.getStatus() != 1) {
                throw new BusinessException("秒杀活动未开启");
            }
            
            // 使用乐观锁扣减数据库库存
            int result = seckillActivityMapper.deductStock(activityId, 1, activity.getVersion());
            if (result == 0) {
                throw new BusinessException("秒杀失败，库存不足或并发冲突");
            }
            
            // 扣减Redis库存
            redisUtil.decrement(stockKey);
            
            // 记录用户参与记录
            String userKey = SECKILL_USER_KEY + activityId + ":" + userId;
            redisUtil.set(userKey, "1", 24, TimeUnit.HOURS);
            
            // 发送消息到RabbitMQ，异步创建订单
            orderMessageSender.sendOrderMessage(userId, activityId);
            
            log.info("秒杀成功 - 用户ID: {}, 活动ID: {}", userId, activityId);
            
        } catch (InterruptedException e) {
            log.error("获取分布式锁被中断", e);
            throw new BusinessException("系统异常");
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    
    @Override
    public void preloadSeckillActivity() {
        // 查询所有进行中的秒杀活动
        List<SeckillActivity> activityList = getSeckillActivityList();
        
        for (SeckillActivity activity : activityList) {
            // 缓存活动信息
            String activityKey = SECKILL_ACTIVITY_CACHE_KEY + activity.getId();
            redisUtil.set(activityKey, JSON.toJSONString(activity), 30, TimeUnit.MINUTES);
            
            // 缓存库存信息
            String stockKey = SECKILL_STOCK_KEY + activity.getId();
            redisUtil.set(stockKey, activity.getSeckillStock(), 30, TimeUnit.MINUTES);
            
            log.info("预加载秒杀活动 - 活动ID: {}, 库存: {}", activity.getId(), activity.getSeckillStock());
        }
    }
    
    @Override
    public boolean checkUserSeckill(Long userId, Long activityId) {
        String userKey = SECKILL_USER_KEY + activityId + ":" + userId;
        Boolean hasKey = redisUtil.hasKey(userKey);
        return hasKey != null && hasKey;
    }
}

