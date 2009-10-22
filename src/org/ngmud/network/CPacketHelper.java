
package org.ngmud.network;

import java.nio.charset.Charset;
import java.io.IOException;


public abstract class CPacketHelper {
	public static final Charset UTF8=Charset.forName("UTF-8");
	public static byte[] StringToBytes(String Str)
	{
		return Str.getBytes(UTF8);
	}
	
	public static String BytesToString(byte Bytes[])
	{
		return new String(Bytes,UTF8);
	}
	
	public static boolean StreamReadBStr(CSocket Sock,byte BStr[],int Len,int StartPos)
	{
		int Pos=StartPos;
		while(Len>0)
		{
			int Read;
			try {
				Read=Sock.in().read(BStr, Pos, Len);
			}
			catch(IOException e)
			{	return false;	}
			Pos+=Read;
			Len-=Read;
		}
		return true;
	}
	
	private CPacketHelper() // Keine Instanz hiervon...
	{}
}
