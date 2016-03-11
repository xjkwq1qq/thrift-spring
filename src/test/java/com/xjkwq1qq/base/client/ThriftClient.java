package com.xjkwq1qq.base.client;

import java.lang.reflect.InvocationTargetException;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import com.upsoft.thrift.hello.HelloService;
import com.upsoft.thrift.hello.HelloService.Iface;
import com.upsoft.thrift.node.NodeService;
import com.xjkwq1qq.core.ThriftClientBuilder;

public class ThriftClient {
	public static void main(String[] args) throws TException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		testBase();
		// testNew();
	}

	private static void testNew() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, TException {
		ThriftClientBuilder builder = new ThriftClientBuilder("localhost", 9091);
		long begin = System.currentTimeMillis();
		HelloService.Iface helloServiceClient = (HelloService.Iface) builder.build(HelloService.Client.class);
		for (int i = 0; i < 100; i++) {
			System.out.println(helloServiceClient.getNode());
		}
		System.out.println(System.currentTimeMillis() - begin);
	}

	private static void testBase() throws TException {
		TSocket transport = new TSocket("localhost", 9091);
		TBinaryProtocol protocol = new TBinaryProtocol(transport);
		long begin = System.currentTimeMillis();
		TProtocol tMultiplexedProtocol = new TMultiplexedProtocol(protocol, "helloService");
		HelloService.Client helloServiceClient = new HelloService.Client(tMultiplexedProtocol);
		for (int i = 0; i < 100; i++) {
			transport.open();
			System.out.println(helloServiceClient.getNode());
			transport.close();

			// TProtocol tMultiplexedProtocol1 = new
			// TMultiplexedProtocol(protocol, "nodeService");
			// transport.open();
			// NodeService.Client nodeServiceClient = new
			// NodeService.Client(tMultiplexedProtocol1);
			// nodeServiceClient.getNode(1);
			// // System.out.println(nodeServiceClient.getNode(1));
			// transport.close();
		}
		System.out.println(System.currentTimeMillis() - begin);
	}
}
