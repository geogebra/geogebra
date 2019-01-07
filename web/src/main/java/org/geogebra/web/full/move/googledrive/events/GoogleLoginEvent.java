package org.geogebra.web.full.move.googledrive.events;

import org.geogebra.common.move.events.BaseEvent;

/**
 * @author gabor Login event for google login
 */
public class GoogleLoginEvent extends BaseEvent {

	private boolean success;

	/**
	 * @param success
	 *            that the login event was successful or not.
	 */
	public GoogleLoginEvent(boolean success) {
		super("google login");
		this.success = success;
	}

	/**
	 * @return if the login event was successfull
	 */
	public boolean isSuccessFull() {
		return success;
	}

}
