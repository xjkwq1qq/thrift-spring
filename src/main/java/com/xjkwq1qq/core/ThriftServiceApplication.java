package com.xjkwq1qq.core;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

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
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.xjkwq1qq.annotation.ThriftService;
import com.xjkwq1qq.util.ClassNameUtil;
import com.xjkwq1qq.util.ThriftServiceUtil;

public class ThriftServiceApplication implements ApplicationContextAware, ApplicationListener<ContextClosedEvent> {
	public static final Logger LOG = LoggerFactory.getLogger(ThriftServiceApplication.class);

	private ApplicationContext applicationContext;
	public static final String DEFAULT_CONFIGSOURCE = "/thrift-spring.properties";
	private String configSource;
	private TServer server;

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

		int port = getPort();
		TServerTransport serverTransport = new TServerSocket(port);
		TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();
		server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(multiplexedProcessor));
		for (Map.Entry<String, Object> entry : thriftServices.entrySet()) {
			Object value = entry.getValue();
			// 获取名称
			//ThriftService thriftService = value.getClass().getAnnotation(ThriftService.class);

			// 获取processor
			TProcessor processor = ThriftServiceUtil.buildProcessor(value);
			// 注册
			String serviceName = ClassNameUtil.getDefaultName(ThriftServiceUtil.getParentClass(value.getClass()));
			if (LOG.isDebugEnabled()) {
				LOG.debug("注册服务组件：" + serviceName + "," + processor.getClass());
			}
			multiplexedProcessor.registerProcessor(serviceName, processor);
		}

		// 启动
		if (LOG.isDebugEnabled()) {
			LOG.debug("启动服务，监听端口：" + port);
		}
		startThrift();
	}

	public void startThrift() {
		new Thread() {
			public void run() {
				server.serve();
			};
		}.start();
	}

	/**
	 * closed event
	 */
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		if (server != null) {
			server.stop();
		}
	}

	/**
	 * 获取thrift启动端口
	 * 
	 * @return thrift启动端口
	 * @throws IOException
	 */
	public int getPort() throws IOException {
		int port = 9090;
		try {
			Properties properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(getConfigSource()));
			String thriftPort = properties.getProperty("thrift.port");
			if (StringUtils.isNotBlank(thriftPort)) {
				port = Integer.valueOf(thriftPort);
			}
		} catch (Exception e) {

		}
		return port;
	}

	private String getConfigSource() {
		if (StringUtils.isBlank(configSource)) {
			configSource = DEFAULT_CONFIGSOURCE;
		}
		return configSource;
	}

	public void setConfigSource(String configSource) {
		this.configSource = configSource;
	}

}
