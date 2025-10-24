package com.seckill.common.annotation;

import java.lang.annotation.*;

/**
 * 限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /**
     * 限流key前缀
     */
    String key() default "rate_limit:";
    
    /**
     * 时间窗口（秒）
     */
    int time() default 60;
    
    /**
     * 限制次数
     */
    int count() default 100;
}

