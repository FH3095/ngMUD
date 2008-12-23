package ngmud.network;

import java.net.*;
import java.io.*;


public class CSocket {
	public CSocket()
	{
		Inited=false;
	}
	
	boolean Init(Socket Sock)
	{
		if(!Sock.isConnected())
		{
			return false;
		}
		
		this.Sock=Sock;
		return InitStreams();
	}
	
	boolean Init(InetAddress Address,int Port,int Timeout,boolean NoDelay)
	{
		try
		{
			Sock=new Socket(Address,Port);
			Sock.setReuseAddress(true);
			Sock.setTcpNoDelay(NoDelay);
			Sock.setSoTimeout(Timeout);
		}
		catch(IOException e)
		{
			return false;
		}
		return InitStreams();
	}
	
	boolean Init(InetAddress Address,int Port,InetAddress LocalAddress,int LocalPort,
				 int Timeout,boolean NoDelay)
	{
		try
		{
			Sock=new Socket(Address,Port,LocalAddress,LocalPort);
			Sock.setReuseAddress(true);
			Sock.setTcpNoDelay(NoDelay);
			Sock.setSoTimeout(Timeout);
		}
		catch(IOException e)
		{
			return false;
		}
		return InitStreams();
	}

	protected boolean InitStreams()
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
		return true;
	}
	
	protected boolean Inited;
	protected Socket Sock;
	protected DataInputStream StreamIn;
	protected DataOutputStream StreamOut;
	protected BufferedInputStream BufIn;
	protected BufferedOutputStream BufOut;
}
