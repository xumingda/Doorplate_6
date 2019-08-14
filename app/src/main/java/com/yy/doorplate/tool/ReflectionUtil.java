package com.yy.doorplate.tool;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ���乤����
 * @author lenovo
 *
 */
public class ReflectionUtil {
	/**
	 * ��ȡobj�����У��ֶ���Ϊname���ֶΡ�ֻ�ܻ�ȡpublic���͵�
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
	 * ��ȡobj�����У��ֶ���Ϊname���ֶΡ�������еġ�privateҲʱ���Ե�
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
	 * �����ȡenum���ͣ�������
	 * @param obj	���ڵ���
	 * @param value ֵ
	 * @param name	�ֶ�����
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
	 * ������ʱ����ָ�����࣬������ָ���ķ���
	 * 
	 * @param cName
	 *            Java������
	 * @param MethodName
	 *            ������
	 * @param params
	 *            �����Ĳ���ֵ
	 * @return
	 */
	public Object Load(String cName, String MethodName, Object[] params) {

		Object retObject = null;

		try {
			// ����ָ������
			Class cls = Class.forName(cName); // ��ȡClass��Ķ���ķ���֮��

			// ����newInstance()��������ȡ���췽����ʵ��
			// Class��newInstance����ֻ�ṩĬ���޲ι���ʵ��
			// Constructor��newInstance�����ṩ���εĹ���ʵ��
			Constructor ct = cls.getConstructor(new Class[]{});
			Object obj = ct.newInstance(new  Object[]{});
			// Object obj = cls.newInstance();

			// ���ݷ�������ȡָ�������Ĳ��������б�
			Class paramTypes[] = this.getParamTypes(cls, MethodName);

			// ��ȡָ������
			Method meth = cls.getMethod(MethodName, paramTypes);
			meth.setAccessible(true);

			// ����ָ���ķ�������ȡ����ֵΪObject����
			retObject = meth.invoke(obj, params);

		} catch (Exception e) {
			System.err.println(e);
		}

		return retObject;
	}

	/**
	 * ��ȡ�������ͣ�����ֵ������Class[]��
	 */
	public  Class[] getParamTypes(Class cls, String mName) {
		Class[] cs = null;

		/*
		 * Note: ��������һ��ͨ��������Ƶ��õķ������Ƿ�public���� �����ڴ˴�ʹ����getDeclaredMethods()����
		 */
		Method[] mtd = cls.getDeclaredMethods();
		for (int i = 0; i < mtd.length; i++) {
			if (!mtd[i].getName().equals(mName)) { // ����������Ҫ�Ĳ������������һ��ѭ��
				continue;
			}

			cs = mtd[i].getParameterTypes();
		}
		return cs;
	}
}
