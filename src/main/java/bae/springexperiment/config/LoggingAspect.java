package bae.springexperiment.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Slf4j
@Component
public class LoggingAspect {

    private static final ThreadLocal<String> traceId = new ThreadLocal<>();
    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

//    @Before("execution(* bae.springexperiment.member.MemberController.*(..))")
//    public void logBefore(JoinPoint joinPoint) {
//        traceId.set(UUID.randomUUID().toString());
//        startTime.set(System.currentTimeMillis());
//        log.info("======= [{}] =======", traceId.get());
//        log.info("[{}] Executing: {}", traceId.get(), joinPoint.getSignature().getName());
//    }
//
//    @After("execution(* bae.springexperiment.member.MemberController.*(..))")
//    public void logAfter(JoinPoint joinPoint) {
//        long duration = System.currentTimeMillis() - startTime.get();
//        log.info("[{}] Completed: {} in {} ms", traceId.get(), joinPoint.getSignature().getName(), duration);
//        log.info("======= [{}] =======", traceId.get());
//        traceId.remove();
//        startTime.remove();
//    }

    @Around("execution(* bae.springexperiment.member.MemberController.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        traceId.set(UUID.randomUUID().toString());
        startTime.set(System.currentTimeMillis());

        log.info("======= [{}] =======", traceId.get());
        log.info("[{}] Executing: {} | Args: {}", traceId.get(), joinPoint.getSignature().getName(), joinPoint.getArgs());

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("[{}] Exception in {} with cause = {}", traceId.get(), joinPoint.getSignature().getName(), throwable.getCause() != null ? throwable.getCause() : "NULL");
            throw throwable;
        }

        long duration = System.currentTimeMillis() - startTime.get();
        log.info("[{}] Completed: {} in {} ms | Result: {}", traceId.get(), joinPoint.getSignature().getName(), duration, result);
        log.info("======= [{}] =======", traceId.get());

        traceId.remove();
        startTime.remove();

        return result;
    }
}
