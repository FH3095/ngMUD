
package ngmud.network;
import java.io.IOException;
import ngmud.network.Packets.*;

public class CPacket {
	protected static final short HEADER_SIZE=6;
	protected short Type;
	protected short SubType;
	protected short Size;
	protected SubPacket Data;
	protected boolean HeaderOK;
	protected boolean DataOK;
	protected CSocket Sock;
	
	public void Clean()
	{
		Data=null;
		Type=SubType=Size=(short)0;
		HeaderOK=DataOK=false;
	}
	
	public boolean Send()
	{
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
	
	public RECIEVED Recv()
	{
		if(DataOK && HeaderOK)
		{	Clean();	}
		RECIEVED Ret=RECIEVED.NOTHING;
		if(!HeaderOK)
		{
			if(Sock.Available()>=HEADER_SIZE)
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
				Data=GetNewPacket();
				Ret=RECIEVED.HEADER;
			}
		}
		if(HeaderOK && !DataOK)
		{
			if(Sock.Available()>=Size)
			{
				if(!Data.Recv(Sock,Size))
				{	return RECIEVED.ERROR;	}
				DataOK=true;
				Ret=RECIEVED.ALL;
			}
		}
		return Ret;
	}
	
	public CPacket(CSocket Sock)
	{
		Clean();
		this.Sock=Sock;
	}
	
	public short GetType()
	{
		if(HeaderOK)
		{
			return Type;
		}
		return -1;
	}
	
	public short GetSubType()
	{
		if(HeaderOK)
		{
			return SubType;
		}
		return -1;
	}
	
	public short GetDateSize()
	{
		if(HeaderOK)
		{
			return Size;
		}
		return -1;
	}
	
	public SubPacket GetData()
	{
		if(DataOK)
		{
			return Data;
		}
		return null;
	}
	
	public void SetType(PACKET_TYPE Type)
	{
		this.Type=(short)Type.ordinal();
		HeaderOK=HeaderComplete();
	}
	
	public void SetSubType(short SubType)
	{
		this.SubType=SubType;
		HeaderOK=HeaderComplete();
	}
	
	protected boolean HeaderComplete()
	{
		return Type!=0 && SubType!=0;
	}
	
	public void SetData(SubPacket Data)
	{
		this.Data=Data;
		DataOK=true;
	}
	
	public void MakePacket(PACKET_TYPE Type,short SubType,SubPacket Data)
	{
		this.Type=(short)Type.ordinal();
		this.SubType=SubType;
		this.Data=Data;
	}
	
	public enum RECIEVED {
		ERROR,NOTHING,HEADER,ALL
	}
	
	public enum PACKET_TYPE {
		MSG_CHAT, MSG_POS, MSG_STAT, SMSG_MAP;
	}
	
	enum CHAT_PACKET {
		SAY, YELL, GROUP, WHISPER, GUILD, CHANNEL, CHANNEL_JOINED, CHANNEL_LEFT,  
	}
	
	protected SubPacket GetNewPacket()
	{
		switch(PACKET_TYPE.values()[Type])
		{
		case MSG_CHAT:
			return new Pack_Chat();
		case MSG_POS:
			break;
		}
		return null;
	}
}
