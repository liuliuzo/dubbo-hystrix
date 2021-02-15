package com.dubbo.hystrix;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

public class DubboHystrixFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboHystrixFilter.class);

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            //消费者方生效
            if (RpcContext.getContext().isConsumerSide()) {
                String methodName = invocation.getMethodName();
                Class<?> interfaceCLz = invoker.getInterface();
                Method method = interfaceCLz.getMethod(methodName, invocation.getParameterTypes());
                AbstractHystrixConfigBuilder builder = AbstractHystrixConfigBuilder.getContext().getBean(AbstractHystrixConfigBuilder.class);
                if (builder != null) {
                    HystrixCommandProperties.Setter commandProperties = builder.buildHystrixCommandProperties(interfaceCLz.getSimpleName(), methodName);
                    HystrixThreadPoolProperties.Setter threadPoolProperties = builder.buildHystrixThreadPoolProperties(interfaceCLz.getSimpleName(), methodName);
                    HystrixMethodConfig config = builder.buildHystrixMethodConfig(interfaceCLz.getSimpleName(), methodName);
                    DubboHystrixCommand command = new DubboHystrixCommand(invoker, invocation, method, commandProperties, threadPoolProperties, config);
                    return command.execute();
                }
            }
        } catch (NoSuchMethodException e) {
            LOGGER.error("DubboHystrixFilter error NoSuchMethod interface:{} methodName:{} parameterTypes:{}"
                    , invoker.getInterface().getName()
                    , invocation.getMethodName()
                    , Arrays.toString(invocation.getParameterTypes())
                    , e);
        }
        return invoker.invoke(invocation);
    }
}
