package com.xjkwq1qq.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;

public class ThriftClientUtil {

	public static final String CLIENT_NAME = "$Client";
	public static final String IFRC_NAME = "$Iface";

	/**
	 * 获取父级类
	 * 
	 * @param clas
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?> getParentClass(Class<?> clas) throws ClassNotFoundException {
		// 获取父类
		String parentClassName = clas.getName().substring(0, clas.getName().indexOf(CLIENT_NAME));
		return Class.forName(parentClassName);
	}

	/**
	 * 获取服务实现对象的集成接口class
	 * 
	 * @param serviceClass
	 * @return
	 */
	public static Class<?> getThriftServiceIfaceClass(Class<?> serviceClass) {
		Class<?>[] interfaceClasses = serviceClass.getInterfaces();
		for (Class<?> interfaceClass : interfaceClasses) {
			if (interfaceClass.getName().endsWith(IFRC_NAME)) {
				return interfaceClass;
			}
		}
		return null;
	}

	public static <A> A newInstance(Class<A> serviceClientClass, TProtocol tProtocol) throws NoSuchMethodException,
			SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<A> constructor = serviceClientClass.getConstructor(TProtocol.class);
		return constructor.newInstance(tProtocol);
	}

}
