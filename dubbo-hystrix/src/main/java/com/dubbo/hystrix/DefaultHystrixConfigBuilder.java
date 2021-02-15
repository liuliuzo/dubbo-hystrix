package com.dubbo.hystrix;

import org.springframework.core.env.Environment;


public class DefaultHystrixConfigBuilder extends AbstractHystrixConfigBuilder {
    @Override
    public HystrixMethodConfig buildHystrixMethodConfig(String classSimpleName, String methodName) {
        HystrixMethodConfig methodConfig = new HystrixMethodConfig();
        methodConfig.setGroupKey(classSimpleName);
        methodConfig.setCommandKey(String.format("%s-%s", classSimpleName, methodName));
        methodConfig.setThreadKey(String.format("%s-%s", classSimpleName, methodName));
        methodConfig.setFallbackClass(getProperty(classSimpleName, "default", "fallbackClass", String.class));
        return methodConfig;
    }

    @Override
    protected <T> T getProperty(String classSimpleName, String methodName, String name, Class<T> type) {
        Environment env = context.getEnvironment();
        return env.getProperty(String.format(PROPERTY_FORMAT, classSimpleName, methodName, name), type);
    }
}
