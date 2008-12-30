package ngmud.network;

import java.net.*;
import java.util.Vector;
import ngmud.ngMUDException;


public class CServer extends Thread {
	protected Vector<CSocket> Socks;
	protected int CurSocksPos;
	protected int CurNewSocksPos;
	protected int ReadingSocks,WritingSocks;
	protected Object CS_ReadingSocks,CS_WritingSocks;

	protected CListenSocket ListSock;
	protected SocketAddress ListenSocketAddress;
	protected int ListenSocketPort;
	protected boolean StopListenSocket;
	
	protected int Cons;
	protected int MaxCons;
	
	protected boolean Inited;
	
	
	public CServer()
	{
		Inited=false;
		ListSock=new CListenSocket(this);
		Socks=null;
		CS_ReadingSocks=new Object();
		CS_WritingSocks=new Object();
	}
	
	protected boolean AcceptNewCon()
	{
		return (MaxCons-Cons) > 0;
	}
	
	public boolean Init(int MaxCons,int ReservCons,int SockPort,SocketAddress SockAddr) throws ngMUDException
	{
		if(ReservCons < 0 || MaxCons < 1 || SockPort < 1)
		{
			throw new ngMUDException("MaxCons and SockPort must be > 0 and ReservCons must be >= 0"+
									 "Cur Values: ReservCons="+ReservCons+", MaxCons="
									 +ReservCons+", SockPort="+SockPort);
		}
		if(Inited)
		{	UnInit();	}
		
		this.MaxCons=MaxCons;
		
		ListenSocketPort=SockPort;
		ListenSocketAddress=SockAddr;
		Cons=0;
		
		Socks=new Vector<CSocket>(0,ReservCons);
		Socks.clear();
		WritingSocks=ReadingSocks=0;
		CurSocksPos=0;
		CurNewSocksPos=0;
		Inited=ListSock.Init(SockPort,SockAddr);
		StopListenSocket=false;
		this.start();
		return Inited;
	}
	
	public void UnInit()
	{
		if(!Inited)
		{	return;	}
		StopListenSocket=true;
		ListSock.UnInit();
		BeginReadSocks();
		for(int i=0;i<Socks.size();i++)
		{
			if(Socks.get(i)!=null)
			{
				Socks.get(i).UnInit();
			}
		}
		Socks.clear();
		Inited=false;
		EndReadSocks();
	}
	
	public void run() // ListenSocket-Handler
	{
		while(!StopListenSocket)
		{
			if(MaxCons-Cons <= 0)
			{
				ListSock.UnInit();
			}
			while(MaxCons-Cons <= 0)
			{
				try {
					Thread.sleep(50);
				}
				catch(InterruptedException e) {}
			}
			ListSock.Init(ListenSocketPort, ListenSocketAddress);

			Socket Sock=ListSock.Accept();
			if(Sock!=null)
			{
				CSocket NewSock=new CSocket();
				NewSock.Init(Sock);
				BeginWriteSocks();
				Socks.add(NewSock);
				EndWriteSocks();
			}
		}
	}
	
	public boolean CleanUp()
	{
		boolean RemovedEntry=false;
		BeginReadSocks();
		for(int i=0;i<Socks.size();i++)
		{
			if(Socks.get(i)==null)
			{
				Socks.remove(i);
				RemovedEntry=true;
			}
		}
		EndReadSocks();
		return RemovedEntry;
	}
	
	public CSocket GetSock(int Pos)
	{
		CSocket Sock;
		BeginReadSocks();
		try {
			Sock=Socks.get(Pos);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			Sock=null;
		}
		EndReadSocks();
		return Sock;
	}
	
	// Max>0 = Max Cons to check, Max < 0 = Complete List checks, Max=0 infinite
	public CSocket NextReadSock(int Max)
	{
		BeginReadSocks();
		if(Socks.isEmpty())
		{
			return null;
		}
		boolean Max0= (Max==0 ? true : false);
		while(Max!=0 || Max0)
		{
			CSocket Check;
			try {
				Check=Socks.elementAt(CurSocksPos);
			}
			catch(ArrayIndexOutOfBoundsException e)
			{
				CurSocksPos=0;
				Check=Socks.elementAt(0);
				if(Max<0)
				{
					Max--;
				}
			}
			CurSocksPos++;
			if(Check.DataAvailable()!=0)
			{
				EndReadSocks();
				return Check;
			}
			if(Max>0)
			{
				Max--;
			}
		}
		EndReadSocks();
		return null;
	}
	
	protected void BeginReadSocks()
	{
		synchronized(CS_ReadingSocks)
		{
			ReadingSocks++;
			if(WritingSocks>0)
			{
				try{
					CS_ReadingSocks.wait();
				}
				catch(InterruptedException e) {}
			}
		}
	}
	
	protected void EndReadSocks()
	{
		synchronized(CS_ReadingSocks)
		{
			ReadingSocks--;
			if(ReadingSocks<=0)
			{
				CS_WritingSocks.notifyAll();
			}
		}
	}
	
	protected void BeginWriteSocks()
	{
		synchronized(CS_ReadingSocks)
		{
			WritingSocks++;
			try {
				CS_WritingSocks.wait();
			}
			catch(InterruptedException e) {}
		}
	}
	
	protected void EndWriteSocks()
	{
		WritingSocks--;
		CS_ReadingSocks.notifyAll();
	}
}
