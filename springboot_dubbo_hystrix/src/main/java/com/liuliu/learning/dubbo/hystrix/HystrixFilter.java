package com.liuliu.learning.dubbo.hystrix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;


@Activate(group = Constants.CONSUMER)
public class HystrixFilter implements Filter {
	
	private static Logger logger = LoggerFactory.getLogger(DubboHystrixCommand.class);
	
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		logger.info("invoke ====================================================");
		DubboHystrixCommand command = new DubboHystrixCommand(invoker, invocation);
		return command.execute();
    }

}
