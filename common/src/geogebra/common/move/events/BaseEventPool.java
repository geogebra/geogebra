package geogebra.common.move.events;

import geogebra.common.move.operations.NetworkOperation;

/**
 * @author gabor
 * Host for the Common event handling
 */
public class BaseEventPool {

	/**
	 * operation of the given event
	 */
	protected NetworkOperation operation = null;
	private boolean online;
	
	/**
	 * Instantiates the Event handling Code
	 * @param op Operation for the given eventPool
	 * @param online whether this should send online or offline events
	 */
	public BaseEventPool(NetworkOperation op, boolean online) {
		this.online = online;
		operation = op;
	}
	
	/**
	 * run over the events, and triggers them.
	 */
	public void trigger(){
		operation.setOnline(this.online);
	}

}
