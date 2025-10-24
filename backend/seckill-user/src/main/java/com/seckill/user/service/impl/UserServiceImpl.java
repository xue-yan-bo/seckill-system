package com.seckill.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.exception.BusinessException;
import com.seckill.common.utils.JwtUtil;
import com.seckill.common.utils.MD5Util;
import com.seckill.model.entity.User;
import com.seckill.user.mapper.UserMapper;
import com.seckill.user.service.UserService;
import com.seckill.user.vo.LoginVO;
import com.seckill.user.vo.RegisterVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterVO registerVO) {
        // 参数校验
        if (!StringUtils.hasText(registerVO.getUsername()) || 
            !StringUtils.hasText(registerVO.getPassword())) {
            throw new BusinessException("用户名和密码不能为空");
        }
        
        // 检查用户名是否已存在
        User existUser = getUserByUsername(registerVO.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        
        // 检查手机号是否已注册
        if (StringUtils.hasText(registerVO.getPhone())) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, registerVO.getPhone());
            User phoneUser = this.getOne(queryWrapper);
            if (phoneUser != null) {
                throw new BusinessException("手机号已注册");
            }
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(registerVO.getUsername());
        user.setPassword(MD5Util.encryptPassword(registerVO.getPassword()));
        user.setNickname(registerVO.getNickname());
        user.setPhone(registerVO.getPhone());
        user.setEmail(registerVO.getEmail());
        user.setStatus(0);
        
        this.save(user);
        log.info("用户注册成功: {}", user.getUsername());
    }
    
    @Override
    public String login(LoginVO loginVO) {
        // 参数校验
        if (!StringUtils.hasText(loginVO.getUsername()) || 
            !StringUtils.hasText(loginVO.getPassword())) {
            throw new BusinessException("用户名和密码不能为空");
        }
        
        // 查询用户
        User user = getUserByUsername(loginVO.getUsername());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 验证密码
        if (!MD5Util.verifyPassword(loginVO.getPassword(), user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() == 1) {
            throw new BusinessException("账号已被禁用");
        }
        
        // 生成JWT Token
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        log.info("用户登录成功: {}", user.getUsername());
        
        return token;
    }
    
    @Override
    public User getUserByUsername(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return this.getOne(queryWrapper);
    }
    
    @Override
    public User getUserByToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        
        // 验证Token
        if (!JwtUtil.validateToken(token)) {
            throw new BusinessException(401, "Token无效或已过期");
        }
        
        // 获取用户ID
        Long userId = JwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            throw new BusinessException(401, "Token解析失败");
        }
        
        // 查询用户
        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        
        return user;
    }
}

