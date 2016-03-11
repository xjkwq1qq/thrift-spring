package com.xjkwq1qq.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;

public class ClassNameUtil {
	/**
	 * 获取默认名称，首字母小些
	 * 
	 * @param clas
	 * @return
	 */
	public static String getDefaultName(Class<? extends Object> clas) {
		String className = clas.getName();
		String tmp[] = className.split("\\.");
		if (tmp.length > 0) {
			String name = tmp[tmp.length - 1];
			if (name.length() == 1) {
				return name.toLowerCase();
			} else {
				return name.substring(0, 1).toLowerCase().concat(name.substring(1));
			}
		}
		return className;
	}

	
}
