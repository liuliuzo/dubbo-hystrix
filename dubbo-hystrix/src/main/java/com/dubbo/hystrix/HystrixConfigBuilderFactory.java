package com.dubbo.hystrix;

/**
 * 熔断器生成工厂接口，实现类配合 AbstractHystrixConfigBuilder使用，逻辑参见 DubboHystrixAutoConfiguration
 * 重要:工厂实现类beanName 必须为hystrixConfigBuilderFactory
 */
public interface HystrixConfigBuilderFactory {
    AbstractHystrixConfigBuilder getHystrixConfigBuilder();
}
