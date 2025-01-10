package org.geogebra.common.move.operations;

import org.geogebra.common.move.views.BooleanRenderable;

/**
 * Base for offline and online operations
 * @author gabor
 */
public class NetworkOperation extends BaseOperation<BooleanRenderable> {
	/**
	 * The Application is online, or not
	 */
	protected boolean online;

	/**
	 * Creates a new offlineOperation class for Offline functionality
	 * 
	 * @param online
	 *            whether the initial state is online
	 */
	public NetworkOperation(boolean online) {
		this.online = online;
	}

	/**
	 * @return if app state is online
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * @param online
	 *            online state Sets the online state of the app (used from
	 *            events)
	 */
	public void setOnline(boolean online) {
		this.online = online;
		dispatchEvent(new BooleanEvent(online));
	}

}
