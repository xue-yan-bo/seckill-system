package com.seckill.common.aspect;

import com.seckill.common.annotation.RateLimit;
import com.seckill.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 限流切面
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Pointcut("@annotation(com.seckill.common.annotation.RateLimit)")
    public void rateLimitPointcut() {}
    
    @Around("rateLimitPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        
        if (rateLimit != null) {
            // 获取请求IP
            String ip = getIpAddress();
            // 生成限流key
            String key = rateLimit.key() + ip + ":" + method.getName();
            
            // 获取当前访问次数
            Object count = redisTemplate.opsForValue().get(key);
            if (count == null) {
                // 第一次访问，设置计数器
                redisTemplate.opsForValue().set(key, 1, rateLimit.time(), TimeUnit.SECONDS);
            } else {
                int currentCount = (Integer) count;
                if (currentCount >= rateLimit.count()) {
                    log.warn("限流提示 - IP: {}, 方法: {}, 超过限制次数: {}", ip, method.getName(), rateLimit.count());
                    throw new BusinessException(429, "访问过于频繁，请稍后再试");
                }
                // 计数器加1
                redisTemplate.opsForValue().increment(key);
            }
        }
        
        return joinPoint.proceed();
    }
    
    /**
     * 获取请求IP地址
     */
    private String getIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("Proxy-Client-IP");
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }
                if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            log.error("获取IP地址失败", e);
        }
        return "unknown";
    }
}

