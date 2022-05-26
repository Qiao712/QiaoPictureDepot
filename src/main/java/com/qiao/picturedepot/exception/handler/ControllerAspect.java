package com.qiao.picturedepot.exception.handler;

import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.exception.EntityNotFoundException;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAspect {
    /**
     * 切入点 controller包及其子包下所有方法
     */
    @Pointcut("execution(* com.qiao.picturedepot.controller..*.*(..))")
    public void controller() {
        System.out.println("切入点签名");
    }

    /**
     * 当controller的返回值为null时，抛出未找到实体异常
     */
    @AfterReturning(pointcut = "controller()", returning = "returnVal")
    public void checkNull(Object returnVal){
        if(returnVal == null){
            throw new EntityNotFoundException();
        }
    }
}
