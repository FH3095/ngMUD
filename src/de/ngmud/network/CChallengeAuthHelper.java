package de.ngmud.network;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.ngmud.CLog;
import de.ngmud.util.CRandom;

public class CChallengeAuthHelper {
	private static CChallengeAuthHelper OnlyInstance=null;
	private final String StdAlgo="SHA-256";
	private MessageDigest DigestCalc;

	private CChallengeAuthHelper()
	{
		DigestCalc=null;
	}
	
	public static CChallengeAuthHelper Instance()
	{
		if(OnlyInstance==null)
		{
			OnlyInstance=new CChallengeAuthHelper();
		}
		return OnlyInstance;
	}
	
	public void Delete()
	{
		OnlyInstance=null;
	}
	
	public boolean Init(String DigestAlgo)
	{
		if(DigestAlgo=="")
		{
			DigestAlgo=StdAlgo;
		}

		try {
	        DigestCalc=MessageDigest.getInstance(DigestAlgo);
        } catch (NoSuchAlgorithmException e) {
        	CLog.Error("Can't use algorithm "+DigestAlgo+" for auth. "+
    			"java.security.MessageDigest doesn't support that algorithm.");
        	return false;
        }
        CLog.Debug("Using "+DigestCalc.getAlgorithm()+" which has "+DigestCalc.getDigestLength()+
        		" Bits provided by "+DigestCalc.getProvider().getName()+".");
        return true;
	}
	
	public byte[] GenerateRandomHash()
	{
		byte Random[]=new byte[12];
		CRandom.Bytes(Random);
		DigestCalc.update(Random);
		return DigestCalc.digest();
	}
	
	public byte[] GenerateHashFromPW(String Username,String PW)
	{
		String ToHash=Username+":"+PW;
		DigestCalc.update(ToHash.getBytes());
		return DigestCalc.digest();
	}
	
	public byte[] GenerateHashFromHashes(byte[] Hash1,byte[] Hash2)
	{
		DigestCalc.update(Hash1);
		DigestCalc.update(":".getBytes());
		DigestCalc.update(Hash2);
		return DigestCalc.digest();
	}
	
	public String HashToString(byte[] Hash)
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
