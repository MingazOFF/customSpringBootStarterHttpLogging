package ru.t1.mingazoff.httpLoggingStarter.aspect;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import ru.t1.mingazoff.httpLoggingStarter.config.HttpLoggingConfigProperties;

@Component
@Aspect
public class MainAspect {
    private HttpLoggingConfigProperties properties;


    public MainAspect(HttpLoggingConfigProperties properties) {
        this.properties = properties;
    }

    Logger log = LogManager.getLogger(MainAspect.class);


    Level level = Level.getLevel(properties.getLevel());


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
        log.log(level, "Execution time [method:{}] in ms:{}", joinPoint.getSignature().toShortString(), finish - start);

        return result;
    }
}
