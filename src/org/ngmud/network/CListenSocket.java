package org.ngmud.network;

import java.io.IOException;
import java.net.*;

import org.ngmud.ngMUDException;


public class CListenSocket{
	public CListenSocket()
	{
		Sock=null;
		Inited=false;
	}
	
	public boolean IsInited()
	{
		return Inited;
	}
	
	public Socket Accept()
	{
		if(!Inited)
		{	return null;	}
		
		try {
			return Sock.accept();
		}
		catch(SocketException e) // Seems like Socket closed
		{
			return null;
		}
		catch(IOException e)
		{
			return null;
		}
	}
	
	public boolean Init(int Port,InetSocketAddress Address,boolean ReUse) throws ngMUDException
	{
		if(Inited)
		{	throw new ngMUDException("CListenSocket already initialized"); }
		try
		{
			Sock=new ServerSocket(Port,0,Address.getAddress());
			Sock.setReuseAddress(ReUse);
		}
		catch(IOException e)
		{
			return false;
		}
		
		try
		{
			Sock.setSoTimeout(0);
		}
		catch(SocketException e)
		{
			return false;
		}
		Inited=true;
		return true;
	}
	
	public void UnInit()
	{
		if(!Inited)
		{	return;	}
		try { Sock.close(); }
		catch(IOException e) {}
		Inited=false;
	}

	protected ServerSocket Sock;
	protected boolean Inited;
}
