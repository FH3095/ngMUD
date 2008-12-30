package ngmud.network;

import java.io.IOException;
import java.net.*;


public class CListenSocket{
	public CListenSocket(CServer Server)
	{
		this.Server=Server;
		Inited=false;
	}
	
	public boolean IsInited()
	{
		return Inited;
	}
	
	public Socket Accept()
	{
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
		Inited=true;
		return true;
	}
	
	public void UnInit()
	{
		try { Sock.close(); }
		catch(IOException e) {}
		Inited=false;
	}

	protected ServerSocket Sock;
	protected CServer Server;
	protected boolean Inited;
}
