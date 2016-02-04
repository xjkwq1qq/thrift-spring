package com.xjkwq1qq.core;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.xjkwq1qq.annotation.ThriftService;

public class ThriftServiceApplication {
	public static final Logger LOG = LoggerFactory.getLogger(ThriftServiceApplication.class);

	private ApplicationContext applicationContext;

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@PostConstruct
	public void init() throws Exception {
		if (LOG.isDebugEnabled()) {
			LOG.debug("扫描thrift组件");
		}
		Map<String, Object> thriftServices = applicationContext.getBeansWithAnnotation(ThriftService.class);
		if (thriftServices == null) {
			return;
		}

		TServerTransport serverTransport = new TServerSocket(9090);
		TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();
		TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(multiplexedProcessor));
		for (Map.Entry<String, Object> entry : thriftServices.entrySet()) {
			String name = entry.getKey();
			Object value = entry.getValue();

			// 获取名称
			ThriftService thriftService = value.getClass().getAnnotation(ThriftService.class);
			if (StringUtils.isNoneBlank(thriftService.value())) {
				name = thriftService.value();
			}
			// 获取processor
			TProcessor processor = ThriftUtil.buildProcessor(value);
			// 注册
			multiplexedProcessor.registerProcessor(name, processor);
		}

		// 启动
		server.serve();
	}

}
