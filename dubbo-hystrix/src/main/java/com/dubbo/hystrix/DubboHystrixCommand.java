package com.dubbo.hystrix;

import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcResult;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

public class DubboHystrixCommand extends HystrixCommand<Result> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DubboHystrixCommand.class);
    private Invoker<?> invoker;
    private Invocation invocation;
    private Method method;
    private String fallbackClass;
    @Getter
    private RpcContext rpcContext;

    DubboHystrixCommand(Invoker invoker, Invocation invocation, Method method, HystrixCommandProperties.Setter commandProperties, HystrixThreadPoolProperties.Setter threadPoolProperties, HystrixMethodConfig config) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(config.getGroupKey()))
                .andCommandKey(HystrixCommandKey.Factory.asKey(config.getCommandKey()))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(config.getThreadKey()))
                .andCommandPropertiesDefaults(commandProperties)
                .andThreadPoolPropertiesDefaults(threadPoolProperties));
        this.invoker = invoker;
        this.invocation = invocation;
        this.fallbackClass = config.getFallbackClass();
        this.method = method;
        this.rpcContext = RpcContext.getContext();
    }

    /**
     * 鍦╤ystrix绾跨▼姹犱腑鎵ц
     *
     * @return dubbo鎵ц缁撴灉
     * @throws RpcException dubbo寮傚父
     */
    protected Result run() throws RpcException {
        //鎶婁富绾跨▼鐨勪笂涓嬫枃copy杩囨潵
        BeanUtils.copyProperties(rpcContext, RpcContext.getContext());
        try {
            return invoker.invoke(invocation);
        } finally {
            //鎷垮埌dubbo鎵ц瀹岀殑鏈�鏂扮殑涓婁笅鏂�
            BeanUtils.copyProperties(RpcContext.getContext(), rpcContext);
            RpcContext.removeContext();
        }
    }

    @Override
    protected Result getFallback() {
        //鎶婁富绾跨▼鐨勪笂涓嬫枃copy杩囨潵
        BeanUtils.copyProperties(rpcContext, RpcContext.getContext());
        RpcResult result = new RpcResult();
        try {
            if (StringUtils.isNotBlank(fallbackClass)) {
                Class<?> clz = Class.forName(fallbackClass);
                if (!invoker.getInterface().isAssignableFrom(clz)) {
                    // fallback绫婚渶瑕佹槸dubbo鎺ュ彛鐨勫疄鐜扮被
                    LOGGER.warn("DubboHystrixCommand getFallback fallbackClass:{} must impl of interface:{}"
                            , fallbackClass
                            , invoker.getInterface().getName());
                    return result;
                }
                //浼樺厛鑾峰彇spring涓殑瀵硅薄
                Object obj = AbstractHystrixConfigBuilder.getContext().getBean(clz);
                if (obj == null) {
                    obj = clz.newInstance();
                }
                Method fallbackMethod = clz.getMethod(method.getName(), method.getParameterTypes());
                if (!fallbackMethod.isAccessible()) {
                    fallbackMethod.setAccessible(true);
                }
                result.setValue(fallbackMethod.invoke(obj, invocation.getArguments()));
            }
        } catch (Exception e) {
            LOGGER.error("DubboHystrixCommand getFallback error fallbackClass:{},fallbackMethod:{},arguments:{}"
                    , fallbackClass
                    , method.getName()
                    , Arrays.toString(invocation.getArguments())
                    , e);
        } finally {
            //鎷垮埌鎵ц瀹岀殑鏈�鏂扮殑涓婁笅鏂�
            BeanUtils.copyProperties(RpcContext.getContext(), rpcContext);
            RpcContext.removeContext();
        }
        return result;
    }
}
