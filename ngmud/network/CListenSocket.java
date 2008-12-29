package ngmud.network;

import java.io.IOException;
import java.net.*;


public class CListenSocket{
	public CListenSocket(CServer Server)
	{
		this.Server=Server;
	}
	
	public boolean Init(int Port,SocketAddress Address)
	{
		try
		{
			Sock=new ServerSocket(Port);
			Sock.bind(Address);
		}
		catch(IOException e)
		{
			return false;
		}
		
		try
		{
			Sock.setReceiveBufferSize(65500);
			Sock.setSoTimeout(0);
		}
		catch(SocketException e)
		{
			return false;
		}
		return true;
	}
	
	void UnInit()
	{
		try { Sock.close(); }
		catch(IOException e) {}
	}

	protected ServerSocket Sock;
	protected CServer Server;
}
