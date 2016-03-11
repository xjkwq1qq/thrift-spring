package com.xjkwq1qq.core;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import com.xjkwq1qq.util.ClassNameUtil;
import com.xjkwq1qq.util.ThriftClientUtil;

public class ThriftClientBuilder {
	private String host;
	private int port;
	private TSocket transport;
	private TBinaryProtocol protocol;

	public ThriftClientBuilder(String host, int port) {
		this.host = host;
		this.port = port;
		transport = new TSocket(host, port);
		protocol = new TBinaryProtocol(transport);
	}

	@SuppressWarnings("unchecked")
	public <A> A build(Class<A> thriftClientClass) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// 获取实例化对象
		Class<?> parentClass = ThriftClientUtil.getParentClass(thriftClientClass);
		TProtocol tMultiplexedProtocol = new TMultiplexedProtocol(protocol, ClassNameUtil.getDefaultName(parentClass));
		A obj = ThriftClientUtil.newInstance(thriftClientClass, tMultiplexedProtocol);
		// 获取iface接口
		Class<?> ifaceClass = ThriftClientUtil.getThriftServiceIfaceClass(thriftClientClass);
		Object proxy = Proxy.newProxyInstance(thriftClientClass.getClassLoader(), new Class[] { ifaceClass }, new ThriftProxyHandler(obj, transport));
		return (A) proxy;
	}

	/**
	 * thrift切面方法
	 *
	 * 文件名称：ThriftClientBuilder.java<br>
	 * 摘要：简要描述本文件的内容<br>
	 * -------------------------------------------------------<br>
	 * 原作者：王强<br>
	 * 完成日期：2016年3月11日<br>
	 */
	public static class ThriftProxyHandler implements InvocationHandler {
		private Object obj;
		private TSocket transport;

		public ThriftProxyHandler(Object obj, TSocket transport) {

			this.obj = obj;
			this.transport = transport;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// 在转调具体目标对象之前，可以执行一些功能处理
			transport.open();
			// 转调具体目标对象的方法
			Object ret = method.invoke(obj, args);
			// 在转调具体目标对象之后，可以执行一些功能处理
			transport.close();
			return ret;
		}
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public TSocket getTransport() {
		return transport;
	}

	public void setTransport(TSocket transport) {
		this.transport = transport;
	}

	public TBinaryProtocol getProtocol() {
		return protocol;
	}

	public void setProtocol(TBinaryProtocol protocol) {
		this.protocol = protocol;
	}

}
