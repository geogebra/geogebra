package org.geogebra.web.full.move.googledrive.events;

import org.geogebra.common.move.events.BaseEvent;

/**
 * Used for notify things if drive loaded
 * 
 * @author gabor
 *
 */
public class GoogleDriveLoadedEvent extends BaseEvent {

	/**
	 * New GD event
	 */
	public GoogleDriveLoadedEvent() {
		super("Drive loaded");
	}

}
