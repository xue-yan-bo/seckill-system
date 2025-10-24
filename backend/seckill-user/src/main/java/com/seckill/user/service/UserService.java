package com.seckill.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.model.entity.User;
import com.seckill.user.vo.LoginVO;
import com.seckill.user.vo.RegisterVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    void register(RegisterVO registerVO);
    
    /**
     * 用户登录
     */
    String login(LoginVO loginVO);
    
    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);
    
    /**
     * 根据Token获取用户信息
     */
    User getUserByToken(String token);
}

