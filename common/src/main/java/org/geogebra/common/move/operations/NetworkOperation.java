package org.geogebra.common.move.operations;

import org.geogebra.common.move.views.BooleanRenderable;

/**
 * @author gabor Base for offline and online operations
 *
 */
public class NetworkOperation extends BaseOperation<BooleanRenderable> {
	/**
	 * The Application is online, or not
	 */
	protected boolean online;

	/**
	 * Creates a new offlineOperation class for Offline functionality
	 * 
	 * @param network
	 *            The implementation of the Network interface
	 */
	public NetworkOperation(Network network) {
		this.online = network.onLine();
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
