package com.seckill.user.controller;

import com.seckill.common.annotation.RateLimit;
import com.seckill.common.annotation.SystemLog;
import com.seckill.common.result.Result;
import com.seckill.model.entity.User;
import com.seckill.user.service.UserService;
import com.seckill.user.vo.LoginVO;
import com.seckill.user.vo.RegisterVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @SystemLog(value = "用户注册", businessType = 1)
    @RateLimit(key = "register:", time = 60, count = 10)
    public Result<?> register(@RequestBody RegisterVO registerVO) {
        userService.register(registerVO);
        return Result.success("注册成功");
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @SystemLog(value = "用户登录", businessType = 0)
    @RateLimit(key = "login:", time = 60, count = 20)
    public Result<Map<String, Object>> login(@RequestBody LoginVO loginVO) {
        String token = userService.login(loginVO);
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        
        return Result.success("登录成功", data);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @SystemLog(value = "获取用户信息", businessType = 0)
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        // 去除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        User user = userService.getUserByToken(token);
        // 清空密码
        user.setPassword(null);
        
        return Result.success(user);
    }
    
    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    @SystemLog(value = "查询用户", businessType = 0)
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
}

