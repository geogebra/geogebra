package org.geogebra.web.full.move.googledrive.events;

import org.geogebra.common.move.events.BaseEvent;

/**
 * @author gabor
 * 
 *         event used to sign out from Google Drive
 *
 */
public class GoogleLogOutEvent extends BaseEvent {

	/**
	 * New logout event.
	 */
	public GoogleLogOutEvent() {
		super("google logout");
	}

}
