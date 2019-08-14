package com.yy.doorplate.tool;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射工具类
 * @author lenovo
 *
 */
public class ReflectionUtil {
	/**
	 * 获取obj对象中，字段名为name的字段。只能获取public类型的
	 * @param obj
	 * @param name
	 * @return
	 */
	public static Object getField(Object obj, String name) {
		Field f;
		Object out = null;
		try {
			f = obj.getClass().getField(name);
			out = f.get(obj);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return out;
	}

	
	/**
	 * 获取obj对象中，字段名为name的字段。针对所有的。private也时可以的
	 * @param obj
	 * @param name
	 * @return
	 */
	public static Object getDeclaredField(Object obj, String name) {
		Field f;
		Object out = null;
		try {
			f = obj.getClass().getDeclaredField(name);
			f.setAccessible(true);
			out = f.get(obj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return out;
	}

	/**
	 * 反射获取enum类型，并设置
	 * @param obj	所在的类
	 * @param value 值
	 * @param name	字段名字
	 */
	@SuppressWarnings("unchecked")
	public static void setEnumField(Object obj, String name, String value) {
		Field f;
		try {
			f = obj.getClass().getField(name);
			Object valuea=Enum.valueOf((Class<Enum>) f.getType(), value);
			f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 在运行时加载指定的类，并调用指定的方法
	 * 
	 * @param cName
	 *            Java的类名
	 * @param MethodName
	 *            方法名
	 * @param params
	 *            方法的参数值
	 * @return
	 */
	public Object Load(String cName, String MethodName, Object[] params) {

		Object retObject = null;

		try {
			// 加载指定的类
			Class cls = Class.forName(cName); // 获取Class类的对象的方法之二

			// 利用newInstance()方法，获取构造方法的实例
			// Class的newInstance方法只提供默认无参构造实例
			// Constructor的newInstance方法提供带参的构造实例
			Constructor ct = cls.getConstructor(new Class[]{});
			Object obj = ct.newInstance(new  Object[]{});
			// Object obj = cls.newInstance();

			// 根据方法名获取指定方法的参数类型列表
			Class paramTypes[] = this.getParamTypes(cls, MethodName);

			// 获取指定方法
			Method meth = cls.getMethod(MethodName, paramTypes);
			meth.setAccessible(true);

			// 调用指定的方法并获取返回值为Object类型
			retObject = meth.invoke(obj, params);

		} catch (Exception e) {
			System.err.println(e);
		}

		return retObject;
	}

	/**
	 * 获取参数类型，返回值保存在Class[]中
	 */
	public  Class[] getParamTypes(Class cls, String mName) {
		Class[] cs = null;

		/*
		 * Note: 由于我们一般通过反射机制调用的方法，是非public方法 所以在此处使用了getDeclaredMethods()方法
		 */
		Method[] mtd = cls.getDeclaredMethods();
		for (int i = 0; i < mtd.length; i++) {
			if (!mtd[i].getName().equals(mName)) { // 不是我们需要的参数，则进入下一次循环
				continue;
			}

			cs = mtd[i].getParameterTypes();
		}
		return cs;
	}
}
