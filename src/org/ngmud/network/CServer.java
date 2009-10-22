package org.ngmud.network;

import java.net.*;
import java.util.Vector;

import org.ngmud.CLog;
import org.ngmud.ngMUDException;
import org.ngmud.callback.CEvent;
import org.ngmud.callback.CEventNotifier;




public class CServer extends Thread {
	public enum WHEN_LISTEN_FULL
	{
		CLOSE, ACCEPT_CLOSE, NOTHING, CALLBACK
	}
	protected CListenSocket ListSock;
	protected InetSocketAddress ListenSocketAddress;
	protected int ListenSocketPort;
	protected WHEN_LISTEN_FULL ListenFullAction;
	protected CEvent<CSocket> ListenFullEvent;
	protected boolean StopListenSocket;
	
	protected Vector<CSocket> NewSocks;
	protected Object CS_NewSocks;
	
	
	protected boolean NewSockNoDelay;
	protected int NewSockTimeout;
	
	protected int Cons;
	protected int MaxCons;
	
	protected boolean Inited;

	
	public CServer()
	{
		Inited=false;
		ListSock=new CListenSocket();
		SetNewSockOptions(false,0);
		CS_NewSocks=new Object();
	}
	
	public boolean SetNewSockOptions(boolean NoDelay,int Timeout)
	{
		if(Inited)
		{	return false;	}
		this.NewSockNoDelay=NoDelay;
		this.NewSockTimeout=Timeout;
		
		return true;
	}
	
	public boolean Init(int MaxCons,WHEN_LISTEN_FULL ListenFullAction,
						CEvent<CSocket> ListenFullEvent,int SockPort,InetSocketAddress SockAddr)
						throws ngMUDException
	{
		if(MaxCons < 1 || SockPort < 1 || SockPort > 0xFFFF)
		{
			throw new ngMUDException("SockPort must be 0 < SockPort <= 0xFFFF and "+
									 "MaxCons must be >= 0"+
									 "Cur Values: MaxCons="+MaxCons+", SockPort="+SockPort);
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
		
		NewSocks=new Vector<CSocket>();
		
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
		NewSocks.clear();
		NewSocks=null;
		Inited=false;
	}
	
	protected synchronized void ConChange(boolean Add)
	{
		if(Add)
		{	Cons++;	}
		else
		{	Cons--;	}
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
							CLog.Error("Can't init listen-socket after closing because server was full.");
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
				CSocket NewSock=new CSocket(this);
				try {
					NewSock.Init(Sock);
				}
				catch(Exception e) {} // Shouldn't happen in a new initialized class
				NewSock.SetSoOptions(NewSockNoDelay, NewSockTimeout);

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
					synchronized(CS_NewSocks)
					{
						NewSocks.add(NewSock);
					}
				}
			}
		}
		StopListenSocket=true;
	}
	
	public Vector<CSocket> GetNewSocks()
	{
		synchronized(CS_NewSocks)
		{
			Vector<CSocket> OldSocks=NewSocks;
			NewSocks=new Vector<CSocket>();
			return OldSocks;
		}
	}
}
