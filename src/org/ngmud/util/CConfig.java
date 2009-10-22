package org.ngmud.util;

import java.io.*;
import java.util.*;

import org.ngmud.CLog;

public class CConfig {
	protected Properties Conf=null;
	
	public boolean Init(String File,boolean XML)
	{
		if(Conf!=null)
		{	return false;	}	
		Conf=new Properties();
		try {
			FileInputStream InStream=new FileInputStream(File);
			if(XML)
			{
				Conf.loadFromXML(InStream);
			}
			else
			{
				Conf.load(InStream);
			}
		}
		catch(IOException e)
		{
			CLog.Error("Can't access File \""+File+"\".");
			Conf=null;
			return false;
		}
		return true;
	}
	
	public boolean Init()
	{
		if(Conf!=null)
		{	return false;	}
		Conf=new Properties();
		return true;
	}
	
	public void UnInit()
	{
		Conf=null;
	}
	
	public boolean Save(String File,String Comments,boolean XML)
	{
		if(XML==true)
		{	return SaveToXML(File,Comments,"UTF-8");	}
		if(Conf==null)
		{	return false;	}
		try {
			Conf.store(new FileOutputStream(File), Comments);
		}
		catch(IOException e)
		{
			CLog.Error("Can't write config to file \""+File+"\".");
			return false;
		}
		return true;
	}
	
	public boolean SaveToXML(String File,String Comments,String Encoding)
	{
		if(Conf==null)
		{	return false;	}
		try {
			Conf.storeToXML(new FileOutputStream(File), Comments, Encoding);
		}
		catch(IOException e)
		{
			CLog.Error("Can't write to XML-Config to file \""+File+"\".");
			return false;
		}
		return true;
	}
	
	public String Get(String Key)
	{
		if(Conf==null)
		{	return null;	}
		return Conf.getProperty(Key);
	}
	
	public String Get(String Key,String Default)
	{
		if(Conf==null)
		{	return null;	}
		return Conf.getProperty(Key,Default);
	}
	
	public boolean Set(String Key,String Val)
	{
		if(Conf==null)
		{	return false;	}
		Conf.setProperty(Key, Val);
		return true;
	}
	
	public Set<String> GetStringKeys()
	{
		if(Conf==null)
		{	return null;	}
		return Conf.stringPropertyNames();
	}
	
	public LinkedList<String> GetKeys()
	{
		if(Conf==null)
		{	return null;	}
		Enumeration<?> E=Conf.propertyNames();
		LinkedList<String> Ret=new LinkedList<String>();
		while(E.hasMoreElements())
		{
			Ret.add((String)E.nextElement());
		}
		return Ret;
	}
	
	public LinkedList<CPair<String,String>> GetKeysAndValues()
	{
		if(Conf==null)
		{	return null;	}
		LinkedList<CPair<String,String> > Ret=new LinkedList<CPair<String,String> >();
		Set<String> Keys=GetStringKeys();
		Iterator<String> It=Keys.iterator();
		while(It.hasNext())
		{
			String Cur=It.next();
			Ret.add(new CPair<String,String>(Cur,Get(Cur)));
		}
		return Ret;
	}
	
	public  CPair<LinkedList<String>,LinkedList<String>> GetKeysAndValuesSeperate()
	{
		if(Conf==null)
		{	return null;	}
		CPair<LinkedList<String>,LinkedList<String>> Ret=
			new CPair<LinkedList<String>,LinkedList<String>>
				(new LinkedList<String>(),new LinkedList<String>());
		Set<String> Keys=GetStringKeys();
		Iterator<String> It=Keys.iterator();
		while(It.hasNext())
		{
			String Cur=It.next();
			Ret.GetFirst().add(Cur);
			Ret.GetSecond().add(Get(Cur));
		}
		return Ret;
	}
}
