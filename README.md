# thrift-spring
实现thrift服务端集成spring，采用TThreadPoolServer同步线程池做默认服务，传输协议采用默认的TBinaryProtocol

集成spring步骤（参考test示例）<br>
1）spring配置中配置<br>
<context:component-scan base-package="com.upsoft.thrift"></context:component-scan>  
<bean class="com.xjkwq1qq.core.ThriftServiceApplication" />
2）在服务实现类上添加注解@ThriftService
如示例中所示：


@ThriftService("HelloService")
public class HelloServiceImpl implements HelloService.Iface {

	@Override
	public String getNode() throws TException {
		return "HelloWorld";
	}
}

服务端便启动完成

