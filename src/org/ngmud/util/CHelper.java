package org.ngmud.util;

import java.util.*;

import org.ngmud.CLog;


public abstract class CHelper {
	private CHelper() {}
	public static Class<?> FindClass(String Packet,String ClassName,boolean Instanceable)
	{
		Class<?> Ret=null;
		try {
			Ret=Class.forName(Packet+ClassName);
		}
		catch(ClassNotFoundException e)
		{
			CLog.Error("Class \""+Packet+ClassName+"\" not found.");
			return null;
		}
		if(Ret!=null && Instanceable==true)
		{
			Object Obj=null;
			try {
				Obj=Ret.newInstance();
			}
			catch(IllegalAccessException e)
			{
				CLog.Error("Can't access class \""+Ret.getName()+"\".");
				return null;
			}
			catch(InstantiationException e)
			{
				CLog.Error("Can't create an instance from \""+Ret.getName()+"\".");
				return null;
			}
			if(Obj==null)
			{
				CLog.Error("Can't create instance for unknown reason from \""+
				           Ret.getName()+"\".");
				return null;
			}
		}
		return Ret;
	}
	public static LinkedList<Class<?>> FindClasses(String Packet,LinkedList<String> Classes,
	                                               boolean Instanceable)
	{
		LinkedList<Class<?>> Ret=new LinkedList<Class<?>>();
		Iterator<String> It=Classes.iterator();
		while(It.hasNext())
		{
			Class<?> C=FindClass(Packet,It.next(),Instanceable);
			if(C!=null)
			{
				Ret.add(C);
			}
		}
		return Ret;
	}
}
