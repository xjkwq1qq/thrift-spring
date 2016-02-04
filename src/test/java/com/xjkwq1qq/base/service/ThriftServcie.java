package com.xjkwq1qq.base.service;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import com.upsoft.thrift.hello.HelloService;
import com.upsoft.thrift.hello.impl.HelloServiceImpl;
import com.upsoft.thrift.node.NodeService;
import com.upsoft.thrift.node.impl.NodeServiceImpl;

public class ThriftServcie {
	public static void main(String[] args) {
		try {
			TServerTransport serverTransport = new TServerSocket(9090);
			TMultiplexedProcessor processor = new TMultiplexedProcessor();
			TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
			processor.registerProcessor("HelloService", new HelloService.Processor<HelloService.Iface>(new HelloServiceImpl()));
			processor.registerProcessor("NodeService", new NodeService.Processor<NodeService.Iface>(new NodeServiceImpl()));
			System.out.println("Starting the simple server...");
			server.serve();

			// TServerTransport serverTransport = new TServerSocket(9090);
			// HelloService.Processor processor = new
			// HelloService.Processor<HelloService.Iface>(new
			// HelloServiceImpl());
			// TServer server = new TSimpleServer(new
			// Args(serverTransport).processor(processor));
			// System.out.println("Starting the simple server...");
			// server.serve();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
