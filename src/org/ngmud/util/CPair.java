package org.ngmud.util;

public class CPair<T,U> {
	protected T First;
	protected U Second;
	
	public CPair(T First,U Second)
	{
		this.First=First;
		this.Second=Second;
	}
	
	public void SetFirst(T First)
	{	this.First=First;	}
	
	public void SetSecond(U Second)
	{	this.Second=Second;	}
	
	public T GetFirst()
	{	return First;	}
	
	public U GetSecond()
	{	return Second;	}
}
