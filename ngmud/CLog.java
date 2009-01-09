package ngmud;

import java.io.*;
import ngmud.ngMUDException;

public class CLog {
	static protected boolean Inited=false;
	static protected DataOutputStream Out=null;
	static protected String Filename="";
	
	static public boolean IsInited()
	{
		return Inited;
	}
	
	static public void Init(String Filename,boolean Overwrite) throws ngMUDException
	{
		if(Filename=="")
		{
			Filename="Log.txt";
		}
		try {
			Out=new DataOutputStream(new FileOutputStream(Filename,!Overwrite));
		}
		catch(IOException e)
		{
			throw new ngMUDException("CRITICAL: CLOG: Can't initialize Log-File-Stream.",e);
		}
		Inited=true;
		CLog.Filename=Filename;
	}
	
	static protected String GetClassMethod()
	{
		StackTraceElement[] STE=Thread.currentThread().getStackTrace();
		return STE[3].getClassName()+"::"+STE[3].getMethodName()+": ";
	}
	
	static public void Error(String Text)
	{
		Out("ERROR: "+GetClassMethod()+Text+"\r\n");
	}
	
	static public void Warning(String Text)
	{
		Out("WARNING: "+GetClassMethod()+Text+"\r\n");
	}
	
	static public void Info(String Text)
	{
		Out("INFO: "+GetClassMethod()+Text+"\r\n");
	}
	
	static public void Debug(String Text)
	{
		Out("DEBUG: "+GetClassMethod()+Text+"\r\n");
	}
	
	static public void Force(String Text)
	{
		Out(GetClassMethod()+Text+"\r\n");
	}
	
	static public void Out(String Text)
	{
		System.out.println(Text);
		try {
			Out.writeBytes(Text);
			Out.flush();
		}
		catch(IOException e) {}
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
