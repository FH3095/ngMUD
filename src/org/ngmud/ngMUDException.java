package org.ngmud;

public class ngMUDException extends Exception {
	private static final long serialVersionUID = 1L; // First version
	
	
	
	public ngMUDException(String Msg)
	{
		super(Msg);
	}
	public ngMUDException(Throwable T)
	{
		super(T);
	}
	public ngMUDException(String Msg,Throwable T)
	{
		super(Msg,T);
	}
}
