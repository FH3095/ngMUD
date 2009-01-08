package ngmud.network;

import java.net.*;
import ngmud.callback.CEvent;
import ngmud.callback.CEventNotifier;
import java.util.Vector;
import ngmud.ngMUDException;


/* WARNING:
 * This class only allows one thread to act with her at once.
 * For example calling UnInit while another Thread 
 */
public class CServer extends Thread {
	public enum WHEN_LISTEN_FULL
	{
		CLOSE, ACCEPT_CLOSE, NOTHING, CALLBACK
	}
	protected Vector<CSocket> Socks;
	protected int CurSocksPos;
	protected int ReadingSocks;
	protected Object CS_ReadingSocks,CS_WritingSocks;

	protected CListenSocket ListSock;
	protected InetSocketAddress ListenSocketAddress;
	protected int ListenSocketPort;
	protected WHEN_LISTEN_FULL ListenFullAction;
	protected CEvent<CSocket> ListenFullEvent;
	protected boolean StopListenSocket;
	
	protected boolean NewSockNoDelay;
	protected int NewSockTimeout;
	
	protected int Cons;
	protected int MaxCons;
	
	protected boolean Inited;

	
	public CServer()
	{
		Inited=false;
		ListSock=new CListenSocket();
		Socks=null;
		CS_ReadingSocks=new Object();
		CS_WritingSocks=new Object();
	}
	
	public boolean Init(int MaxCons,int ReservCons,WHEN_LISTEN_FULL ListenFullAction,
						CEvent<CSocket> ListenFullEvent,int SockPort,InetSocketAddress SockAddr)
						throws ngMUDException
	{
		if(ReservCons < 0 || MaxCons < 1 || SockPort < 1 || SockPort > 0xFFFF)
		{
			throw new ngMUDException("SockPort must be 0 < SockPort <= 0xFFFF, "+
									 "ReservCons and MaxCons must be >= 0"+
									 "Cur Values: ReservCons="+ReservCons+", MaxCons="
									 +ReservCons+", SockPort="+SockPort);
		}
		if(Inited)
		{	throw new ngMUDException("CServer already inited");	}
		
		this.MaxCons=MaxCons;
		this.ListenFullAction=ListenFullAction;
		this.ListenFullEvent=ListenFullEvent;
		
		ListenSocketPort=SockPort;
		ListenSocketAddress=SockAddr;
		StopListenSocket=false;
		Cons=0;
		
		Socks=new Vector<CSocket>(0,ReservCons);
		Socks.clear();
		ReadingSocks=0;
		CurSocksPos=0;
		Inited=ListSock.Init(SockPort,SockAddr,ListenFullAction==WHEN_LISTEN_FULL.CLOSE);
		this.start();
		return Inited;
	}
	
	public void UnInit()
	{
		if(!Inited)
		{	return;	}
		StopListenSocket=true;
		ListSock.UnInit();
		while(StopListenSocket)
		{
				Thread.yield();
		}
		for(int i=0;i<Socks.size();i++)
		{
			if(Socks.get(i)!=null && Socks.get(i).IsInited())
			{
				Socks.get(i).UnInit();
			}
		}
		Socks.clear();
		Socks=null; // Free for garbage Collection
		Inited=false;
	}
	
	public void run() // ListenSocket-Handler
	{
		while(!StopListenSocket)
		{
			if(MaxCons-Cons <= 0)
			{
				if(ListenFullAction==WHEN_LISTEN_FULL.CLOSE)
				{
					ListSock.UnInit();
					while(MaxCons-Cons <= 0 && !StopListenSocket)
					{
							Thread.yield();
					}
					if(!StopListenSocket)
					{
						try {
							ListSock.Init(ListenSocketPort, ListenSocketAddress,true);
						}
						catch(ngMUDException e)
						{
							// TODO: Write to Log
						}
					}
				}
				else if(ListenFullAction==WHEN_LISTEN_FULL.NOTHING)
				{
					while(MaxCons-Cons <= 0 && !StopListenSocket)
					{
							Thread.yield();
					}
				}
			}
			
			Socket Sock=ListSock.Accept();
			if(Sock!=null)
			{
				CSocket NewSock=new CSocket();
				try {
					NewSock.Init(Sock);
				}
				catch(Exception e) {} // Shouldn't happen in a new initialized class
				if(MaxCons-Cons <= 0 && ListenFullAction==WHEN_LISTEN_FULL.ACCEPT_CLOSE)
				{
					NewSock.UnInit();
				}
				else if(MaxCons-Cons <= 0 && ListenFullAction==WHEN_LISTEN_FULL.CALLBACK)
				{
					CEventNotifier<CSocket> EventNot=new CEventNotifier<CSocket>(0,NewSock,ListenFullEvent);
					EventNot.Fire();
				}
				else if(MaxCons-Cons > 0)
				{
					boolean Continue=true;
					synchronized(CS_WritingSocks)
					{
						do {
							synchronized(CS_ReadingSocks)
							{
								Continue=ReadingSocks>0;
							}
							Thread.yield();
						} while(Continue);
						Socks.add(NewSock);
					}
					Cons++;
				}
			}
		}
		StopListenSocket=true;
	}
	
	public boolean CleanUp()
	{
		boolean RemovedEntry=false;
		boolean Continue=true;
		synchronized(CS_WritingSocks)
		{
			do {
				synchronized(CS_ReadingSocks)
				{
					Continue=ReadingSocks>0;
				}
				Thread.yield();
			} while(Continue);
			for(int i=0;i<Socks.size();i++)
			{
				if(Socks.get(i)==null || !Socks.get(i).IsInited())
				{
					Socks.remove(i);
					RemovedEntry=true;
				}
			}
		}
		return RemovedEntry;
	}
	
	public CSocket GetSock(int Pos)
	{
		if(Pos>=Socks.size())
		{	return null;	}
		CSocket Sock;
		Sock=Socks.get(Pos); // Vector is internally synchronized, no need to synch here
		return Sock.IsInited() ? Sock : null;
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
			Check=Socks.elementAt(CurSocksPos);
			CurSocksPos++;
			if(CurSocksPos>=Socks.size())
			{
				CurSocksPos=0;
				if(Max<0)
				{
					Max++;
				}
			}
			if(Check!=null && Check.DataAvailable()!=0)
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
		synchronized(CS_WritingSocks) // Flaschenhals, ja, ich weiﬂ. Geht leider nicht anders.
		{
			synchronized(CS_ReadingSocks)
			{
				ReadingSocks++;
			}
		}
	}
	
	protected void EndReadSocks()
	{
		synchronized(CS_WritingSocks)
		{
			synchronized(CS_ReadingSocks)
			{
				ReadingSocks++;
			}
		}
	}
}
