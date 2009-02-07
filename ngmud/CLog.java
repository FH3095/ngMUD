package ngmud;

import java.io.*;
import ngmud.ngMUDException;

public class CLog {
	static protected boolean Inited=false;
	static protected DataOutputStream Out=null;
	static protected String Filename="";
	static protected short LogLevel=0;
	
	static public boolean IsInited()
	{
		return Inited;
	}
	
	static public void Init(String Filename,boolean Overwrite,short LogLevel) throws ngMUDException
	{
		if(LogLevel<0)
		{
			throw new ngMUDException("CRITICAL: CLog: LogLevel<0 is not allowed");
		}
		if(Filename=="")
		{
			Filename="Log.txt";
		}
		try {
			Out=new DataOutputStream(new FileOutputStream(Filename,!Overwrite));
		}
		catch(IOException e)
		{
			throw new ngMUDException("CRITICAL: CLog: Can't initialize Log-File-Stream.",e);
		}
		Inited=true;
		CLog.Filename=Filename;
		CLog.LogLevel=LogLevel;
	}
	
	static protected String GetClassMethod()
	{
		StackTraceElement[] STE=Thread.currentThread().getStackTrace();
		return STE[3].getClassName()+"::"+STE[3].getMethodName()+": ";
	}
	
	static public void Error(String Text)
	{
		if(1<=CLog.LogLevel)
		{
			Out("ERROR: "+GetClassMethod()+Text);
		}
	}
	
	static public void Warning(String Text)
	{
		if(2<=CLog.LogLevel)
		{
			Out("WARNING: "+GetClassMethod()+Text);
		}
	}
	
	static public void Info(String Text)
	{
		if(3<=CLog.LogLevel)
		{
			Out("INFO: "+GetClassMethod()+Text);
		}
	}
	
	static public void Debug(String Text)
	{
		if(4<=CLog.LogLevel)
		{
			Out("DEBUG: "+GetClassMethod()+Text);
		}
	}
	
	static public void Force(String Text)
	{
		Out(GetClassMethod()+Text);
	}
	
	static public void Force(String Text,short LogLevel)
	{
		if(LogLevel<=CLog.LogLevel)
		{
			Out(GetClassMethod()+Text);
		}
	}
	
	static public void Out(String Text)
	{
		System.out.println(Text+"\r\n");
		try {
			Out.writeBytes(Text+"\r\n");
			Out.flush();
		}
		catch(IOException e) {}
	}
	
	static public void Out(String Text,short LogLevel)
	{
		if(LogLevel<=CLog.LogLevel)
		{
			Out(Text);
		}
	}
	
	static public void UnInit()
	{
		if(Out!=null)
		{
			try {
				Out.close();
			}
			catch(IOException e) {}
			Out=null;
		}
	}
}
