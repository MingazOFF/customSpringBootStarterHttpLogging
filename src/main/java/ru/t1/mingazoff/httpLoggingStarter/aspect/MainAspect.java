package ru.t1.mingazoff.httpLoggingStarter.aspect;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import ru.t1.mingazoff.httpLoggingStarter.config.HttpLoggingConfigProperties;

import java.net.http.HttpResponse;

@Aspect
public class MainAspect {

    private final Level level;

    public MainAspect(HttpLoggingConfigProperties properties) {

        if (properties == null || properties.getLevel().isEmpty()) {
            level = Level.getLevel("INFO");
        } else {
            level = Level.getLevel(properties.getLevel());
        }

    }

    Logger log = LogManager.getLogger(MainAspect.class);


    @Before("@annotation(LogBefore)")
    public void logBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            log.log(level, "Calling method:{}, arg(s):{}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        } else {
            log.log(level, "Calling method:{}", joinPoint.getSignature().toShortString());
        }

    }

    @AfterThrowing(pointcut = "@annotation(LogAfterThrowing)",
            throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.log(level, "Exception throwing in method:{}, message:{}", joinPoint.getSignature().toShortString(),
                exception.getMessage());
    }

    @AfterReturning(pointcut = "@annotation(LogAfterReturning)",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.log(level, "Method:{} returning:{}", joinPoint.getSignature().toShortString(), result);
    }

    @Around("@annotation(LogAround)")
    public Object logAround(ProceedingJoinPoint joinPoint) {
        long start = System.currentTimeMillis();
        log.log(level, "Calling method:{}", joinPoint.getSignature().toShortString());
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception throwing in method:{}, message:{}",
                    joinPoint.getSignature().toShortString(), e.getMessage());
            throw new RuntimeException();
        }
        long finish = System.currentTimeMillis();
        HttpResponse.ResponseInfo responseInfo = (HttpResponse.ResponseInfo) result;
        int status = responseInfo.statusCode();
        log.log(level, "Execution time [method:{}] in ms:{}", joinPoint.getSignature().toShortString(), finish - start);
        log.log(level, "Response statusCode: {}", status);
        return result;
    }
}
