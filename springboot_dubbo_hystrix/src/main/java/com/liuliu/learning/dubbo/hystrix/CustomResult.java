package com.liuliu.learning.dubbo.hystrix;

import java.util.Map;
import com.alibaba.dubbo.rpc.Result;

public class CustomResult implements Result {
	
	@Override
	public Object getValue() {
		return null;
	}

	@Override
	public Throwable getException() {
		return null;
	}

	@Override
	public boolean hasException() {
		return false;
	}

	@Override
	public Object recreate() throws Throwable {
		return null;
	}

	@Override
	public Object getResult() {
		return null;
	}

	@Override
	public Map<String, String> getAttachments() {
		return null;
	}

	@Override
	public String getAttachment(String s) {
		return null;
	}

	@Override
	public String getAttachment(String s, String s1) {
		return null;
	}
}
