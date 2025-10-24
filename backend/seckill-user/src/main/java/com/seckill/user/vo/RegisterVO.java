package com.seckill.user.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 注册请求VO
 */
@Data
public class RegisterVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
}

