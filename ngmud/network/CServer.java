package ngmud.network;

import java.net.*;
import java.util.Vector;
import ngmud.ngMUDException;

public class CServer extends Thread {
	protected Vector<CSocket> Socks;
	protected int CurSocksPos;
	protected int CurNewSocksPos;

	protected CListenSocket ListSock;
	protected SocketAddress ListenSocketAddress;
	protected int ListenSocketPort;
	
	protected int Cons;
	protected int MaxCons;
	protected int ReservCons;
	
	protected boolean Inited;
	
	
	public CServer()
	{
		Inited=false;
		ListSock=new CListenSocket(this);
		Socks=new Vector<CSocket>();
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
		this.ReservCons=ReservCons;
		
		ListenSocketPort=SockPort;
		ListenSocketAddress=SockAddr;
		Cons=0;
		
		Socks.clear();
		CurSocksPos=0;
		CurNewSocksPos=0;
		Inited=ListSock.Init(SockPort,SockAddr);
		this.start();
		return Inited;
	}
	
	public void UnInit()
	{
		if(!Inited)
		{	return;	}
		ListSock.UnInit();
		for(int i=0;i<Socks.size();i++)
		{
			if(Socks.get(i)!=null)
			{
				Socks.get(i).UnInit();
			}
		}
		Socks.clear();
		Inited=false;
	}
	
	public void run() // ListenSocket-Handler
	{
		// This method should accept new connections and close/open
		// the ListenSocket when new connections are allowed/not allowed.
	}
	
	public boolean CleanUp()
	{
		boolean RemovedEntry=false;
		for(int i=0;i<Socks.size();i++)
		{
			if(Socks.get(i)==null)
			{
				Socks.remove(i);
				RemovedEntry=true;
			}
		}
		if(ReservCons!=0)
		{
			int NewSize=Socks.size()+(ReservCons-(Socks.size()%ReservCons));
			Socks.setSize(NewSize);
		}
		return RemovedEntry;
	}
	
	public CSocket GetSock(int Pos)
	{
		try {
			return Socks.get(Pos);
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
	 // Max>0 = Max Cons to check, Max < 0 = Complete List checks, Max=0 infinite
	public CSocket NextReadSock(int Max)
	{
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
				return Check;
			}
			if(Max>0)
			{
				Max--;
			}
		}
		return null;
	}
}
