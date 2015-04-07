package org.geogebra.common.move.events;

import org.geogebra.common.move.operations.NetworkOperation;

/**
 * @author gabor Host for the Common event handling
 */
public class BaseEventPool {

	/**
	 * operation of the given event
	 */
	protected final NetworkOperation operation;
	private final boolean online;

	/**
	 * Instantiates the Event handling Code
	 * 
	 * @param op
	 *            Operation for the given eventPool
	 * @param online
	 *            whether this should send online or offline events
	 */
	public BaseEventPool(NetworkOperation op, boolean online) {
		this.online = online;
		this.operation = op;
	}

	/**
	 * run over the events, and triggers them.
	 */
	public void trigger() {
		this.operation.setOnline(this.online);
	}

}
