package ngmud.network.Packets;

import java.io.IOException;

import ngmud.network.CPacketHelper;
import ngmud.network.CSocket;

public class Pack_Chat extends SubPacket {
	public long From;
	public byte Content[];
	
	public boolean Recv(CSocket Sock,int Size)
	{
		try {
			From=Sock.in().readLong();
			Size-=8;
			Content=new byte[Size];
			if(!CPacketHelper.StreamReadBStr(Sock, Content, Size, 0))
			{	return false;	}
		} catch(IOException e)
		{	return false;	}
		
		return true;
	}
	
	public boolean Send(CSocket Sock)
	{
		try {
			Sock.out().writeLong(From);
			Sock.out().write(Content);
		}
		catch(IOException e)
		{	return false;	}
		return true;
	}
	
	public short GetSize()
	{
		return (short)(4+Content.length);
	}
}