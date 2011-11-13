package org.concord.framework.startable;

public interface StartableListener 
{
	/**
	 * In some cases the state of the simulation will not be updated when the 
	 * event is sent to the listener.  So for example during a started event
	 * the event.getStartable().isRunning() might return false.
	 * 
	 * @param event
	 */
	public void startableEvent(StartableEvent event);
}
