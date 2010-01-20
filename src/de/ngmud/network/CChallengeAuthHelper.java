package de.ngmud.network;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import de.ngmud.CLog;

public class CChallengeAuthHelper {
	private static CChallengeAuthHelper OnlyInstance=null;
	private MessageDigest DigestCalc;
	public final String STD_ALGO="SHA-256";
	public final int RND_BYTE_ARRAY_LEN=12;

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
			DigestAlgo=STD_ALGO;
		}

		try {
	        DigestCalc=MessageDigest.getInstance(DigestAlgo);
        } catch (NoSuchAlgorithmException e) {
        	CLog.Error("Can't use algorithm "+DigestAlgo+" for auth. "+
    			"java.security.MessageDigest doesn't support that algorithm.");
        	return false;
        }
        CLog.Debug("Using "+DigestCalc.getAlgorithm()+" which has "+DigestCalc.getDigestLength()+
        		" Bytes provided by "+DigestCalc.getProvider().getName()+".");
        return true;
	}
	
	public byte[] GenerateHashFromPW(String PW)
	{
		return DigestCalc.digest(PW.getBytes());
	}
	
	public byte[] GenerateResultHash(byte[] PWHash,byte[] Rnd)
	{
		DigestCalc.update(PWHash);
		return DigestCalc.digest(Rnd);
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
