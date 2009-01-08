package ngmud.network;

import java.net.*;
import java.io.*;
import ngmud.ngMUDException;


public class CSocket {
	public CSocket()
	{
		Sock=null;
		Inited=false;
	}
	
	public synchronized boolean IsInited()
	{
		return Inited;
	}
	
	public synchronized int DataAvailable()
	{
		if(!Inited)
		{	return -1;	}
		try
		{
			return BufIn.available();
		}
		catch(IOException e)
		{
			return -1;
		}
	}
	
	protected synchronized void ThrowInitException() throws ngMUDException
	{
		throw new ngMUDException("Socket already initialized.");
	}
	
	public synchronized boolean Init(Socket Sock) throws ngMUDException
	{
		if(Inited)
		{
			ThrowInitException();
		}
		if(!Sock.isConnected())
		{
			return false;
		}
		
		this.Sock=Sock;
		return InitStreams();
	}
	
	public synchronized boolean SetSoOptions(boolean NoDelay,int Timeout)
	{
		if(!Inited)
		{
			return false;
		}
		try
		{
			Sock.setTcpNoDelay(NoDelay);
			Sock.setSoTimeout(Timeout);
		}
		catch(IOException e)
		{
			return false;
		}
		return true;
	}
	
	public synchronized boolean Init(InetSocketAddress Address,int Port,
									 InetSocketAddress LocalAddress,int LocalPort,int Timeout,
									 boolean NoDelay) throws ngMUDException
	{
		if(Inited)
		{
			ThrowInitException();
		}
		try
		{
			Sock=new Socket(Address.getAddress(),Port,LocalAddress.getAddress(),LocalPort);
		}
		catch(IOException e)
		{
			return false;
		}
		return InitStreams();
	}

	protected synchronized boolean InitStreams()
	{
		if(!Sock.isConnected())
		{
			return false;
		}
		
		try
		{
			BufIn=new BufferedInputStream(this.Sock.getInputStream());
			BufOut=new BufferedOutputStream(this.Sock.getOutputStream());
			StreamIn=new DataInputStream(BufIn);
			StreamOut=new DataOutputStream(BufOut);
		}
		catch(IOException e)
		{
			return false;
		}
		Inited=true;
		return true;
	}
	
	public synchronized void UnInit()
	{
		try {
			StreamIn.close();
			StreamIn=null;
			StreamOut.close();
			StreamOut=null;
			BufIn.close();
			BufIn=null;
			BufOut.close();
			BufOut=null;
			Sock.close();
			Sock=null;

			Inited=false;
		}
		catch (IOException e)
		{
		}
	}
	
	protected boolean Inited;
	protected Socket Sock;
	protected DataInputStream StreamIn;
	protected DataOutputStream StreamOut;
	protected BufferedInputStream BufIn;
	protected BufferedOutputStream BufOut;
}
