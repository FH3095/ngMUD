package de.ngmud;

import java.io.*;


public class CLog extends Thread {
	public enum LOG_LEVEL {
		CUSTOM_FORCE(-2,"Custom Force"),
		FORCE(-1,"Force"),
		ONLY_CUSTOM(0,"Custom"),
		ERROR (1,"<b><span style=\"color: #FF0000\">Error</span></b>"),
		WARNING (2,"<b><span style=\"color: #CCCC00\">Warning</span></b>"),
		INFO (3,"<b><span style=\"color: #00BB00\">Info</span></b>"),
		DEBUG (4,"Debug"),
		CUSTOM (5,"<b>Custom</b>");
		
		
		private final int Num;
		private final String HtmlStr;
		LOG_LEVEL(int Num,String HtmlStr)
		{
			this.Num=Num;
			this.HtmlStr=HtmlStr;
		}
		
		public int GetNum()
		{
			return Num;
		}
		
		public String GetHtmlText()
		{
			return HtmlStr;
		}
	}
	
	static protected boolean Inited=false;
	static protected DataOutputStream TxtOut=null;
	static protected DataOutputStream HtmlOut=null;
	static protected String Filename="";
	static protected LOG_LEVEL LogLevel;
	static protected int CustomLogLevel;
	
	static private String TxtBuffer="",HtmlBuffer="";
	static private Thread MainThread;
	static private Object Mutex;
	
	private CLog()	{}

	static public boolean IsInited()
	{
		return Inited;
	}
	
	public void run()
	{
		while(MainThread.isAlive())
		{
			synchronized(Mutex)
			{
				try {
					Mutex.wait(10000);
				}
				catch(InterruptedException e) {}
				if(!TxtBuffer.isEmpty())
				{
					System.out.println(TxtBuffer);
					try {
						TxtOut.writeBytes(TxtBuffer);
					}
					catch(IOException e) {}
					TxtBuffer="";
				}
				if(!HtmlBuffer.isEmpty())
				{
					try {
						HtmlOut.writeBytes(HtmlBuffer);
					}
					catch(IOException e) {}
					HtmlBuffer="";
				}
			}
			try {
				System.out.flush();
				TxtOut.flush();
				HtmlOut.flush();
			}
			catch(IOException e)
			{}
		}
		UnInit();
	}
	
	static public void Init(String Filename,boolean Overwrite,LOG_LEVEL LogLevel,int CustomLogLevel) throws ngMUDException
	{
		if(LogLevel.GetNum()<0)
		{
			throw new ngMUDException("CRITICAL: Log-Levels less than zero are not acceptable Log-Levels");
		}
		if(Filename=="")
		{
			Filename="Log";
		}
		try {
			TxtOut=new DataOutputStream(new FileOutputStream(Filename+".txt",!Overwrite));
			HtmlOut=new DataOutputStream(new FileOutputStream(Filename+".html",false));
		}
		catch(IOException e)
		{
			throw new ngMUDException("CRITICAL: CLog: Can't initialize Log-File-Stream.",e);
		}
		try {
			HtmlOut.writeBytes("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n"+
			                   "\t\"http://www.w3.org/TR/html4/loose.dtd\">\n"+
			                   "<html>\n<head>\n"+
			                   "\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">\n"+
			                   "\t<title>LogFile</title>\n"+
			                   "</head>\n\n"+
			                   "<body>\n<center><h1>LogFile</h1></center><br>\n"+
			                   "<table border=\"3\">");
			HtmlOut.writeBytes("<tr><th>Type</th><th>Level</th><th>Method</th><th>Message</th></tr>");
		}
		catch(IOException e) { }
		Inited=true;
		CLog.Filename=Filename;
		CLog.LogLevel=LogLevel;
		CLog.CustomLogLevel=CustomLogLevel;
		
		Mutex=new Object();
		
		CLog Log=new CLog();
		Log.start();
		MainThread=Thread.currentThread();
	}
	
	static protected String GetClassMethod()
	{
		StackTraceElement[] STE=Thread.currentThread().getStackTrace();
		return STE[3].getClassName()+"::"+STE[3].getMethodName();
	}
	
	static public void Error(String Text)
	{
		if(LOG_LEVEL.ERROR.GetNum()<=CLog.LogLevel.GetNum())
		{
			String ClassMethod=GetClassMethod();
			Out(LOG_LEVEL.ERROR,LOG_LEVEL.ERROR.GetNum(),ClassMethod,Text);
		}
	}
	
	static public void Warning(String Text)
	{
		if(LOG_LEVEL.WARNING.GetNum()<=CLog.LogLevel.GetNum())
		{
			String ClassMethod=GetClassMethod();
			Out(LOG_LEVEL.WARNING,LOG_LEVEL.WARNING.GetNum(),ClassMethod,Text);
		}
	}
	
	static public void Info(String Text)
	{
		if(LOG_LEVEL.INFO.GetNum()<=CLog.LogLevel.GetNum())
		{
			String ClassMethod=GetClassMethod();
			Out(LOG_LEVEL.INFO,LOG_LEVEL.INFO.GetNum(),ClassMethod,Text);
		}
	}
	
	static public void Debug(String Text)
	{
		if(LOG_LEVEL.DEBUG.GetNum()<=CLog.LogLevel.GetNum())
		{
			String ClassMethod=GetClassMethod();
			Out(LOG_LEVEL.DEBUG,LOG_LEVEL.DEBUG.GetNum(),ClassMethod,Text);
		}
	}
	
	static public void CustomForce(String Text)
	{
		if(LOG_LEVEL.CUSTOM.GetNum()<=CLog.LogLevel.GetNum() ||
		   LOG_LEVEL.ONLY_CUSTOM.GetNum()==CLog.LogLevel.GetNum())
		{
			String ClassMethod=GetClassMethod();
			Out(LOG_LEVEL.CUSTOM_FORCE,-1,ClassMethod,Text);
		}
	}
	
	static public void Custom(String Text,int Level)
	{
		if((LOG_LEVEL.CUSTOM.GetNum()<=CLog.LogLevel.GetNum() ||
		    LOG_LEVEL.ONLY_CUSTOM.GetNum()<=CLog.LogLevel.GetNum()) &&
		   Level<=CLog.CustomLogLevel)
		{
			String ClassMethod=GetClassMethod();
			Out(LOG_LEVEL.CUSTOM,Level,ClassMethod,Text);
		}
	}
	
	static public void Force(String Text)
	{
		String ClassMethod=GetClassMethod();
		Out(LOG_LEVEL.FORCE,-1,ClassMethod,Text);
	}
	
	static private void Out(LOG_LEVEL Type,int Level,String Method,String Text)
	{
		String HtmlText=Text;
		HtmlText=HtmlText.replace("<", "&lt;");
		HtmlText=HtmlText.replace(">", "&gt;");
		synchronized(Mutex)
		{
			TxtBuffer+=Method+": "+Text+"\n";
			HtmlBuffer+="<tr><td>"+Type.GetHtmlText()+"</td><td>"+Level+"</td><td>"+
	    	Method+"</td><td>"+HtmlText+"</td></tr>\n";
			Mutex.notifyAll();
		}
	}
	
	public void UnInit()
	{
		if(TxtOut!=null)
		{
			try {
				TxtOut.close();
			}
			catch(IOException e) {}
			TxtOut=null;
		}
		if(HtmlOut!=null)
		{
			try {
				HtmlOut.writeBytes("\n</table><br><br><br>\n\n\n"+
				                   "<small>&copy; <a href=\"http://ngMUD.ath.cx\">ngMUD</a>"+
				                   "</small><br>\n</body>\n</html>\n");
				HtmlOut.close();
			}
			catch(IOException e) {}
			HtmlOut=null;
		}
	}
}
