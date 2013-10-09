package geogebra.web.move.googledrive.events;

import geogebra.common.move.events.BaseEvent;

/**
 * @author gabor
 *	Login event for google login
 */
public class GoogleLoginEvent extends BaseEvent {

	private boolean success;
	
	/**
	 * @param success that the login event was successfull or not.
	 */
	public GoogleLoginEvent(boolean success) {
		this.success = false;
	}
	
	/**
	 * @return if the login event was successfull
	 */
	public boolean isSuccessFull() {
		return success;
	}

	@Override
	public void trigger() {
		//do nothing
	}

}
