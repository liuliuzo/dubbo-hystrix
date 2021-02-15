package com.dubbo.hystrix;

import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class AbstractHystrixConfigBuilder implements ApplicationContextAware {
    protected static final String PROPERTY_FORMAT = "pandora.hystrix.%s.%s.%s";

    protected static ApplicationContext context;

    public HystrixCommandProperties.Setter buildHystrixCommandProperties(String classSimpleName, String methodName) {
        HystrixCommandProperties.Setter setter = HystrixCommandProperties.Setter().withExecutionTimeoutEnabled(false);
        // circuitBreakerErrorThresholdPercentage 失败率达到多少熔断
        Integer circuitBreakerErrorThresholdPercentage = getProperty(classSimpleName, methodName, "circuitBreakerErrorThresholdPercentage", Integer.class);
        if (circuitBreakerErrorThresholdPercentage != null) {
            setter.withCircuitBreakerErrorThresholdPercentage(circuitBreakerErrorThresholdPercentage);
        }
        // circuitBreakerSleepWindowInMilliseconds 熔断器开启后关闭的间隔时间 放入流量
        Integer circuitBreakerSleepWindowInMilliseconds = getProperty(classSimpleName, methodName, "circuitBreakerSleepWindowInMilliseconds", Integer.class);
        if (circuitBreakerSleepWindowInMilliseconds != null) {
            setter.withCircuitBreakerSleepWindowInMilliseconds(circuitBreakerSleepWindowInMilliseconds);
        }
        // circuitBreakerRequestVolumeThreshold 10秒钟达到多少次请求开启熔断功能
        Integer circuitBreakerRequestVolumeThreshold = getProperty(classSimpleName, methodName, "circuitBreakerRequestVolumeThreshold", Integer.class);
        if (circuitBreakerRequestVolumeThreshold != null) {
            setter.withCircuitBreakerRequestVolumeThreshold(circuitBreakerRequestVolumeThreshold);
        }
        // fallbackEnabled 是否启动降级策略
        Boolean fallbackEnabled = getProperty(classSimpleName, methodName, "fallbackEnabled", Boolean.class);
        if (fallbackEnabled != null) {
            setter.withFallbackEnabled(fallbackEnabled);
        }
        // circuitBreakerEnabled 是否启用熔断策略
        Boolean circuitBreakerEnabled = getProperty(classSimpleName, methodName, "circuitBreakerEnabled", Boolean.class);
        if (circuitBreakerEnabled != null) {
            setter.withCircuitBreakerEnabled(circuitBreakerEnabled);
        }
        // circuitBreakerForceOpen 强制熔断
        Boolean circuitBreakerForceOpen = getProperty(classSimpleName, methodName, "circuitBreakerForceOpen", Boolean.class);
        if (circuitBreakerForceOpen != null) {
            setter.withCircuitBreakerForceOpen(circuitBreakerForceOpen);
        }
        // circuitBreakerForceClosed 强制关闭熔断
        Boolean circuitBreakerForceClosed = getProperty(classSimpleName, methodName, "circuitBreakerForceClosed", Boolean.class);
        if (circuitBreakerForceClosed != null) {
            setter.withCircuitBreakerForceClosed(circuitBreakerForceClosed);
        }
        return setter;
    }

    /**
     * @param methodName      方法名
     * @param classSimpleName dubbo接口类名
     * @return
     */
    public HystrixThreadPoolProperties.Setter buildHystrixThreadPoolProperties(String classSimpleName, String methodName) {
        HystrixThreadPoolProperties.Setter setter = HystrixThreadPoolProperties.Setter();
        // coreSize 线程池核心大小
        Integer coreSize = getProperty(classSimpleName, methodName, "coreSize", Integer.class);
        if (coreSize != null) {
            setter.withCoreSize(coreSize);
        }
        Integer keepAliveTimeMinutes = getProperty(classSimpleName, methodName, "keepAliveTimeMinutes", Integer.class);
        if (keepAliveTimeMinutes != null) {
            setter.withKeepAliveTimeMinutes(keepAliveTimeMinutes);
        }
        // queueSizeRejectionThreshold 线程池队列达到多少时拒绝请求
        Integer queueSizeRejectionThreshold = getProperty(classSimpleName, methodName, "queueSizeRejectionThreshold", Integer.class);
        if (queueSizeRejectionThreshold != null) {
            setter.withQueueSizeRejectionThreshold(queueSizeRejectionThreshold);
        }
        return setter;
    }

    public abstract HystrixMethodConfig buildHystrixMethodConfig(String classSimpleName, String methodName);

    protected abstract <T> T getProperty(String classSimpleName, String methodName, String name, Class<T> type);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
