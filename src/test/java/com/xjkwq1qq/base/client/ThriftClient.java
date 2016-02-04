package com.xjkwq1qq.base.client;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;

import com.upsoft.thrift.hello.HelloService;
import com.upsoft.thrift.node.NodeService;

public class ThriftClient {
	public static void main(String[] args) throws TException {
		TSocket transport = new TSocket("localhost", 9090);
		TBinaryProtocol protocol = new TBinaryProtocol(transport);
		long begin = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			transport.open();
			TProtocol tMultiplexedProtocol = new TMultiplexedProtocol(protocol, "HelloService");
			HelloService.Client helloServiceClient = new HelloService.Client(tMultiplexedProtocol);
			System.out.println(helloServiceClient.getNode());
			transport.close();

			transport.open();
			TProtocol tMultiplexedProtocol1 = new TMultiplexedProtocol(protocol, "NodeService");
			NodeService.Client nodeServiceClient = new NodeService.Client(tMultiplexedProtocol1);
			System.out.println(nodeServiceClient.getNode(1));
			transport.close();
		}
		System.out.println(System.currentTimeMillis()-begin);
	}
}
