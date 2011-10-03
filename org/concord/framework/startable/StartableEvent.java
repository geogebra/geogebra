package org.concord.framework.startable;

public class StartableEvent {
	public enum StartableEventType{
		/**
		 * This startable has just started running, usually this is sent after the start method of the 
		 * startable is called.
		 */
		STARTED,

		/**
		 * This startable has just stopped running, usually this is sent after the stop method of the 
		 * startable is called.
		 */
		STOPPED,
		
		/**
		 * This startable has just been reset, usually this is sent after the reset method of the 
		 * startable is called.
		 */
		RESET,
		
		/**
		 * The startable was updated in some way outside of the usual start, stop, reset events so its
		 * isRunning, isInInitialState, and getStartableInfo might have changed.
		 */
		UPDATED 
	}
	
	StartableEventType type;
	Startable startable;

	// This is helpful to know for a started event
	boolean wasInInitialState;
	
	public StartableEvent() {
	}
	
	public StartableEvent(StartableEventType type, Startable startable) {
		this.type = type;
		this.startable = startable;
	}

	public void setType(StartableEventType type) {
		this.type = type;
	}
	
	public StartableEventType getType() {
		return type;
	}
	
	public void setStartable(Startable startable) {
		this.startable = startable;
	}
	
	public Startable getStartable() {
		return startable;
	}
	
	public void setWasInInitialState(boolean wasInInitialState) {
		this.wasInInitialState = wasInInitialState;
	}
	
	public boolean getWasInInitialState() {
		return wasInInitialState;
	}
	
	public StartableEvent clone() {
		StartableEvent clone = new StartableEvent(type, startable);
		clone.setWasInInitialState(wasInInitialState);
		return clone;
	}
}
