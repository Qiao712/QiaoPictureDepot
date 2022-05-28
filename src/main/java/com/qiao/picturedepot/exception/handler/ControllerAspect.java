package com.qiao.picturedepot.exception.handler;

import com.qiao.picturedepot.exception.EntityNotFoundException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAspect {
    /**
     * 切入点 controller包及其子包下所有方法
     */
    @Pointcut("execution(* com.qiao.picturedepot.controller..*.*(..))")
    public void restAPI() {
    }

    /**
     * 当controller的返回值为null时，抛出未找到实体异常
     */
    @AfterReturning(pointcut = "restAPI()", returning = "returnVal")
    public void checkNull(JoinPoint joinPoint, Object returnVal){
        //获取返回值类型
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Class<?> returnType = methodSignature.getReturnType();

        //若返回类型不为空，且返回了null，则抛出未找到实体的异常
        if(returnType != void.class && returnVal == null){
            throw new EntityNotFoundException(returnType);
        }
    }
}
