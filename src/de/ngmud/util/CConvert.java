package de.ngmud.util;

public class CConvert {
	private CConvert()
	{}
	
	public static String HashToString(byte[] Hash)
	{
		final String HEXES = "0123456789abcdef";
		StringBuilder Ret=new StringBuilder(2*Hash.length);
		for(byte b:Hash)
		{
			Ret.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt(b & 0x0F));
		}
		return Ret.toString();
	}

}
