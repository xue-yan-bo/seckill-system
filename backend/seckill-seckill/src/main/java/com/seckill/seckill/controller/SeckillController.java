package com.seckill.seckill.controller;

import com.seckill.common.annotation.RateLimit;
import com.seckill.common.annotation.SystemLog;
import com.seckill.common.result.Result;
import com.seckill.common.utils.JwtUtil;
import com.seckill.model.entity.SeckillActivity;
import com.seckill.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 秒杀控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/seckill")
@CrossOrigin(origins = "*")
public class SeckillController {
    
    @Autowired
    private SeckillService seckillService;
    
    /**
     * 获取秒杀活动列表
     */
    @GetMapping("/list")
    @SystemLog(value = "查询秒杀活动列表", businessType = 0)
    public Result<List<SeckillActivity>> getSeckillActivityList() {
        List<SeckillActivity> list = seckillService.getSeckillActivityList();
        return Result.success(list);
    }
    
    /**
     * 获取秒杀活动详情
     */
    @GetMapping("/{activityId}")
    @SystemLog(value = "查询秒杀活动详情", businessType = 0)
    public Result<SeckillActivity> getSeckillActivityById(@PathVariable Long activityId) {
        SeckillActivity activity = seckillService.getSeckillActivityById(activityId);
        return Result.success(activity);
    }
    
    /**
     * 执行秒杀
     */
    @PostMapping("/{activityId}")
    @SystemLog(value = "执行秒杀", businessType = 1)
    @RateLimit(key = "seckill:", time = 1, count = 1) // 每秒最多1次请求
    public Result<?> doSeckill(
            @PathVariable Long activityId,
            @RequestHeader("Authorization") String token) {
        
        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        // 获取用户ID
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未登录或Token无效");
        }
        
        seckillService.doSeckill(userId, activityId);
        return Result.success("秒杀成功，订单创建中...");
    }
    
    /**
     * 检查用户是否已参与秒杀
     */
    @GetMapping("/check/{activityId}")
    public Result<Boolean> checkUserSeckill(
            @PathVariable Long activityId,
            @RequestHeader("Authorization") String token) {
        
        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return Result.error(401, "未登录或Token无效");
        }
        
        boolean checked = seckillService.checkUserSeckill(userId, activityId);
        return Result.success(checked);
    }
    
    /**
     * 预加载秒杀活动（管理员接口）
     */
    @PostMapping("/preload")
    @SystemLog(value = "预加载秒杀活动", businessType = 0)
    public Result<?> preloadSeckillActivity() {
        seckillService.preloadSeckillActivity();
        return Result.success("预加载成功");
    }
}

