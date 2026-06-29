package com.teamsync.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamsync.entity.OperationLog;
import com.teamsync.mapper.OperationLogMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private OperationLogMapper operationLogMapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* com.teamsync.controller..*.*(..))")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            saveLog(joinPoint, duration, exception);
        }
    }

    private void saveLog(ProceedingJoinPoint joinPoint, long duration, Exception exception) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return;

            HttpServletRequest request = attributes.getRequest();
            Long userId = (Long) request.getAttribute("userId");
            String username = (String) request.getAttribute("username");

            OperationLog log = new OperationLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setOperation(getOperationDescription(joinPoint));
            log.setMethod(request.getMethod() + " " + request.getRequestURI());
            log.setIp(getClientIp(request));
            operationLogMapper.insert(log);

            logger.info("请求: {} {}, 用户: {}, 耗时: {}ms, 参数: {}", 
                    request.getMethod(), request.getRequestURI(), 
                    username != null ? username : "匿名", 
                    duration, Arrays.toString(joinPoint.getArgs()));
        } catch (Exception e) {
            logger.error("记录日志失败", e);
        }
    }

    private String getOperationDescription(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + methodName;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
