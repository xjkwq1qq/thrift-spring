package com.upsoft.thrift.hello.impl;

import org.apache.thrift.TException;

import com.upsoft.thrift.hello.HelloService;
import com.xjkwq1qq.annotation.ThriftService;

@ThriftService
public class HelloServiceImpl implements HelloService.Iface {

	@Override
	public String getNode() throws TException {
		return "HelloWorld";
	}

}
