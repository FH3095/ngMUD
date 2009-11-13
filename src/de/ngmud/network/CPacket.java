package de.ngmud.network;

import java.io.*;

import de.ngmud.network.packets.*;


public class CPacket {
	protected static final short HEADER_SIZE=6;
	protected short Type;
	protected short SubType;
	protected short Size;
	protected CSubPacket Data;
	protected boolean HeaderOK;
	protected boolean DataOK;
	protected CSocket Sock;
	
	public void Clean()
	{
		Data=null;
		Type=SubType=Size=(short)-1;
		HeaderOK=DataOK=false;
	}
	
	public boolean Send(CSocket Sock)
	{
		if(Sock==null || !DataOK || !HeaderOK)
		{	return false;	}
		
		try {
			Sock.out().writeShort(Type);
			Sock.out().writeShort(SubType);
			Sock.out().writeShort(Data.GetSize());
		}
		catch(IOException e)
		{
			return false;
		}
		return Data.Send(Sock);
	}
	
	public boolean Send()
	{
		return Send(Sock);
	}
	
	public RECIEVED Recv(CSocket Sock)
	{
		if(Sock==null)
		{	return RECIEVED.ERROR;	}
		
		if(DataOK && HeaderOK)
		{	Clean();	}
		RECIEVED Ret=RECIEVED.NOTHING;
		if(!HeaderOK)
		{
			if(Sock.DataAvailable()>=HEADER_SIZE)
			{
				try
				{
					Type=Sock.in().readShort();
					SubType=Sock.in().readShort();
					Size=Sock.in().readShort();
				}
				catch(IOException e)
				{	return RECIEVED.ERROR;	}
				HeaderOK=true;
				Data=CPacketMgr.GetNewPacket(Type);
				Ret=RECIEVED.HEADER;
			}
		}
		if(HeaderOK && !DataOK)
		{
			if(Sock.DataAvailable()>=Size)
			{
				if(!Data.Recv(Sock,Size))
				{	return RECIEVED.ERROR;	}
				DataOK=true;
				Ret=RECIEVED.ALL;
			}
		}
		return Ret;
	}
	
	public RECIEVED Recv()
	{
		return Recv(Sock);
	}
	
	public CPacket(CSocket Sock)
	{
		Clean();
		this.Sock=Sock;
	}
	
	public CPacket()
	{
		Clean();
		Sock=null;
	}
	
	public void SetSock(CSocket Sock)
	{
		this.Sock=Sock;
	}
	
	public CSocket GetSock()
	{
		return Sock;
	}
	
	public short GetType()
	{
		if(HeaderOK && Sock!=null)
		{
			return Type;
		}
		return -1;
	}
	
	public short GetSubType()
	{
		if(HeaderOK && Sock!=null)
		{
			return SubType;
		}
		return -1;
	}
	
	public short GetDateSize()
	{
		if(HeaderOK && Sock!=null)
		{
			return Size;
		}
		return -1;
	}
	
	public CSubPacket GetData()
	{
		if(DataOK && Sock!=null)
		{
			return Data;
		}
		return null;
	}
	
	public void SetType(short Type)
	{
		this.Type=Type;
		HeaderOK=HeaderComplete();
	}
	
	public void SetSubType(short SubType)
	{
		this.SubType=SubType;
		HeaderOK=HeaderComplete();
	}
	
	protected boolean HeaderComplete()
	{
		return Type!=-1 && SubType!=-1;
	}
	
	public void SetData(CSubPacket Data)
	{
		this.Data=Data;
		DataOK=true;
	}
	
	public void MakePacket(short Type,short SubType,CSubPacket Data)
	{
		this.Type=Type;
		this.SubType=SubType;
		this.Data=Data;
	}
	
	public enum RECIEVED {
		ERROR,NOTHING,HEADER,ALL
	}
}
