package org.geogebra.common.move.ggtapi.events;

import org.geogebra.common.move.events.BaseEvent;

/**
 * User loged out from GGB account
 * 
 * @author gabor
 */
public class LogOutEvent extends BaseEvent {
	/**
	 * Creates a new LogOutEvent
	 */
	public LogOutEvent() {
		super("logout");
	}

}
