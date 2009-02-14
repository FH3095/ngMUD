package ngmud.network.Packets;

import ngmud.network.CSocket;

public abstract class SubPacket {
	public boolean Recv(CSocket Sock,int Size)
	{
		return false;
	}
	public boolean Send(CSocket Sock)
	{
		return false;
	}
	
	public short GetSize()
	{
		return -1;
	}
}
