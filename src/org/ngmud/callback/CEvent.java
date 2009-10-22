package org.ngmud.callback;

public abstract class CEvent<T> {
	public abstract void EventHappened(int Val,T Object);
}
