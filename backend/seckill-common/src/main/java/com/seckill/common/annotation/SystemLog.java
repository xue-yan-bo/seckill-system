package com.seckill.common.annotation;

import java.lang.annotation.*;

/**
 * 系统日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLog {
    
    /**
     * 操作描述
     */
    String value() default "";
    
    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    int businessType() default 0;
}

