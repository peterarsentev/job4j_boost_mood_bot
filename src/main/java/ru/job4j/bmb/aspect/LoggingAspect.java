package ru.job4j.bmb.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* ru.job4j.bmb.services.*.*(..))")
    public void logBeforeMethodExecution(JoinPoint joinPoint) {
        var signature = joinPoint.getSignature();
        Object[] argsObj = joinPoint.getArgs();
        String args = Arrays.toString(argsObj);
        String methodName = signature.toShortString();
        LOGGER.info("Вызван метод:  {}", methodName );
        LOGGER.info("аргументы: {}", args);
    }
}
