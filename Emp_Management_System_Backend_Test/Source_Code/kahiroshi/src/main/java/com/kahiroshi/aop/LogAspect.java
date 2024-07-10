package com.kahiroshi.aop;

import com.google.gson.Gson;
import com.kahiroshi.mapper.OperateLogMapper;
import com.kahiroshi.object.OperateLog;
import com.kahiroshi.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class LogAspect {

    //Request use to get user id from jwt
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private OperateLogMapper operateLogMapper;

    @Around("@annotation(com.kahiroshi.anno.Log)")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {

        System.out.println("In aop.......................................");
        Gson gson = new Gson();

        //Get info
        String jwt = request.getHeader("token");
        Claims claims = JwtUtils.parseKey(jwt);
        Integer operateUser = (Integer) claims.get("id");
        LocalDateTime operateTime = LocalDateTime.now();
        String methodName = joinPoint.getTarget().getClass().getName()
                + "." + joinPoint.getSignature().getName();
        String methodParams = Arrays.toString(joinPoint.getArgs());

        //Run method
        Object result = joinPoint.proceed();
        String resultValue = gson.toJson(result);

        //Put info into object
        OperateLog operateLog = new OperateLog(
                null, operateUser, operateTime, methodName, methodParams, resultValue);
        System.out.println("Log:" + operateLog);
        //Call mapper
        operateLogMapper.insertLog(operateLog);
        return result;
    }
}
