package org.ngmud.callback;

public class CEventNotifier<T> {
	private CEvent<T> Event;
	private int Val;
	private T Obj;
	
	public CEventNotifier(int Val,T Obj,CEvent<T> Event)
	{
		this.Event=Event;
		this.Val=Val;
		this.Obj=Obj;
	}
	
	public void Fire()
	{
		Event.EventHappened(Val, Obj);
	}
}
