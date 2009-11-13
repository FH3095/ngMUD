package de.ngmud.network.packets;

import de.ngmud.network.CSocket;

public abstract class CSubPacket {
	public static final short PACK_NUM=0;
	public enum SUB_TYPE {}
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
