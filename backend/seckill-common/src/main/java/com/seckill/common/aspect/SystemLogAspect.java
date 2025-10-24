package com.seckill.common.aspect;

import com.alibaba.fastjson2.JSON;
import com.seckill.common.annotation.SystemLog;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 系统日志切面
 */
@Slf4j
@Aspect
@Component
public class SystemLogAspect {
    
    @Pointcut("@annotation(com.seckill.common.annotation.SystemLog)")
    public void logPointcut() {}
    
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SystemLog systemLog = method.getAnnotation(SystemLog.class);
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        
        // 记录请求日志
        log.info("==================== 系统日志 开始 ====================");
        log.info("操作描述: {}", systemLog.value());
        log.info("业务类型: {}", getBusinessTypeName(systemLog.businessType()));
        log.info("请求方法: {}.{}", joinPoint.getTarget().getClass().getName(), method.getName());
        if (request != null) {
            log.info("请求URL: {}", request.getRequestURL().toString());
            log.info("请求方式: {}", request.getMethod());
            log.info("请求IP: {}", getIpAddress(request));
        }
        log.info("请求参数: {}", JSON.toJSONString(joinPoint.getArgs()));
        
        // 执行方法
        Object result = null;
        Exception exception = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long executeTime = endTime - startTime;
            
            // 记录响应日志
            if (exception != null) {
                log.error("执行异常: {}", exception.getMessage());
            } else {
                log.info("返回结果: {}", JSON.toJSONString(result));
            }
            log.info("执行耗时: {}ms", executeTime);
            log.info("==================== 系统日志 结束 ====================");
        }
        
        return result;
    }
    
    /**
     * 获取业务类型名称
     */
    private String getBusinessTypeName(int businessType) {
        switch (businessType) {
            case 1: return "新增";
            case 2: return "修改";
            case 3: return "删除";
            default: return "其它";
        }
    }
    
    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
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
}

