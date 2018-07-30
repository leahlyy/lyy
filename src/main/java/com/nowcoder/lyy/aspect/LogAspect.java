package com.nowcoder.lyy.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect         //AOP面向切面，面向所有服务都需要做的
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    //在执行com.nowcoder.lyy.controller.IndexController中所有的方法之前都要先执行这个方法,
    // 如果把IndexController改为*Controller,则执行所有的..Controller，这里的*是正则表达，表示所有的
    @Before("execution(* com.nowcoder.lyy.controller.*Controller.*(..))")
    public void beforeMethod(JoinPoint joinPoint){
        StringBuilder sb = new StringBuilder();
        for(Object arg : joinPoint.getArgs()){
            sb.append("arg:" + arg.toString() + "|");
        }
        logger.info("before method:" + sb.toString());
    }

    //在执行com.nowcoder.lyy.controller.IndexController中所有的方法之后都要执行这个方法
    @After("execution(* com.nowcoder.lyy.controller.IndexController.*(..))")
    public void afterMethod(JoinPoint joinPoint){
        logger.info("after method:");
    }
}
