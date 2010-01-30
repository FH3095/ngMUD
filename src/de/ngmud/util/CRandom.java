package de.ngmud.util;

import java.util.Random;

public class CRandom {
	private static Random Rand=null;

	/**
	 * Returns a Random-object and seeds it with System.nanoTime() when created.
	 * @return A Random-object
	 */
	private static Random R()
	{
		if(Rand==null)
		{
			Rand=new Random(System.nanoTime());
		}
		return Rand;
	}

	/**
	 * Calls R();
	 * @return Random-object
	 */
	public static Random GetRand()
	{
		return R();
	}
	
	/**
	 * No instances from this class.
	 */
	private CRandom() {
	}
	
	/**
	 * Returns a random double from 0.0 (inclusive) to 1.0 (exclusive).
	 * @return Random double
	 */
	public static double Double()
	{
		return R().nextDouble();
	}
	
	/**
	 * Returns a random double from Min (inclusive) to Max (exclusive).
	 * @param Min 
	 * @param Max
	 * @return Random double
	 */
	public static double Double(double Min,double Max)
	{
		return (R().nextDouble()*(Max-Min))+Min;
	}
	
	public static float Float()
	{
		return R().nextFloat();
	}
	
	public static float Float(float Min,float Max)
	{
		return (R().nextFloat()*(Max-Min))+Min;
	}
	
	public static int Int()
	{
		return R().nextInt();
	}
	
	/**
	 * Returns a random int between Min(inclusive) and Max(inclusive).
	 * This function only uses 31 bits of the integer.
	 * @param Min
	 * @param Max
	 * @return Random int
	 */
	public static int Int(int Min,int Max)
	{
		return (Math.abs(R().nextInt())%((Max-Min)+1))+Min;
	}
	
	public static long Long()
	{
		return R().nextLong();
	}
	
	public static long Long(long Min,long Max)
	{
		return (Math.abs(R().nextLong())%((Max-Min)+1))+Min;
	}

	public static boolean Bool()
	{
		return R().nextBoolean();
	}
	
	public static double Gaussian()
	{
		return R().nextGaussian();
	}
	
    public static StringBuffer RandomString(int MinLen, int MaxLen)
    {
    	int Len;
    	if(MinLen==MaxLen)
    	{
    		Len=MinLen;
    	}
    	else
    	{
    		Len=Int(MinLen,MaxLen);
    	}
    	StringBuffer Ret = new StringBuffer(Len);
    	int What;
    	int Char='0';
    	for(int i=0;i<Len;i++)
    	{
    		What=Int(0,2);
    		switch(What)
    		{
    		case 0:
    			Char='0'+Int(0,9);
    			break;
    		case 1:
    			Char='a'+Int(0,25);
    			break;
    		case 2:
    			Char='A'+Int(0,25);
    			break;
    		}
    		Ret.append((char)Char);
    	}
    	return Ret;
    }

    public static void Bytes(byte[] Bytes)
    {
    	R().nextBytes(Bytes);
    }
    
    public static byte[] Bytes(int MinLen,int MaxLen)
    {
    	int Len;
    	if(MinLen==MaxLen)
    	{
    		Len=MinLen;
    	}
    	else
    	{
    		Len=Int(MinLen,MaxLen);
    	}
    	byte[] Ret=new byte[Len];
    	Bytes(Ret);
    	return Ret;
    }
}
