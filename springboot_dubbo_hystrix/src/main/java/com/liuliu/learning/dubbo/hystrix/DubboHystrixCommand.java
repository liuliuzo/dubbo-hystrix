package com.liuliu.learning.dubbo.hystrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;

public class DubboHystrixCommand extends HystrixCommand<Result> {

	private static Logger logger = LoggerFactory.getLogger(DubboHystrixCommand.class);
	
	private Invoker<?> invoker;
	private Invocation invocation;
    
    private static int circuitBreakerRequestVolumeThreshold = 5;
    //private static int coreSize = 20;
    private static int circuitBreakerErrorThresholdPercentage = 50;
    private static int circuitBreakerSleepWindowInMilliseconds = 20000;
    
    private static final int DEFAULT_THREADPOOL_CORE_SIZE = 30;
    
    public DubboHystrixCommand(Invoker<?> invoker,Invocation invocation){
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(invoker.getInterface().getName()))
        			.andCommandKey(HystrixCommandKey.Factory.asKey(String.format("%s_%d", invocation.getMethodName(),invocation.getArguments() == null ? 0 : invocation.getArguments().length)))
              .andCommandPropertiesDefaults(getCommandProperties(invoker))
              //.andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(invoker.getUrl().getParameter("coreSize",coreSize)))
        	  .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties.Setter().withCoreSize(getThreadPoolCoreSize(invoker.getUrl()))));//线程池数;
        this.invoker=invoker;
        this.invocation=invocation;
    }
    
	private static HystrixCommandProperties.Setter getCommandProperties(Invoker<?> invoker) {
		int threshold = invoker.getUrl().getParameter("circuitBreakerRequestVolumeThreshold",circuitBreakerRequestVolumeThreshold);
		int sleep = invoker.getUrl().getParameter("circuitBreakerSleepWindowInMilliseconds",circuitBreakerSleepWindowInMilliseconds);
		int percentage = invoker.getUrl().getParameter("circuitBreakerErrorThresholdPercentage",circuitBreakerErrorThresholdPercentage);
		return  HystrixCommandProperties.Setter()
                .withCircuitBreakerRequestVolumeThreshold(threshold)
                .withCircuitBreakerSleepWindowInMilliseconds(sleep)
                .withCircuitBreakerErrorThresholdPercentage(percentage)
                .withExecutionTimeoutEnabled(false);
	}
    
    @Override
    protected Result run() throws Exception {
        return invoker.invoke(invocation);
    }
    
	private static int getThreadPoolCoreSize(URL url) {
		if (url != null) {
			int size = url.getParameter("ThreadPoolCoreSize", DEFAULT_THREADPOOL_CORE_SIZE);
			if (logger.isDebugEnabled()) {
				logger.debug("ThreadPoolCoreSize:" + size);
			}
			return size;
		}
		return DEFAULT_THREADPOOL_CORE_SIZE;
	}

//	@Override
//	protected Result getFallback() {
//		logger.info("getFallback >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//		return new CustomResult();
//	}
    
//    @Override
//    protected Result getFallback() {
//    	logger.error("getFallback >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
//    	return super.getFallback();
//    }

	//  以下四种情况将触发getFallback调用：
	//  (1):run()方法抛出非HystrixBadRequestException异常。
	//  (2):run()方法调用超时
	//  (3):熔断器开启拦截调用
	//  (4):线程池/队列/信号量是否跑满
    @Override
    protected Result getFallback() {
    	logger.info("getFallback >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        return null;
    }
	
}
