package geogebra.common.move.ggtapi.events;

import geogebra.common.move.events.BaseEvent;

/**
 * @author gabor
 *  Creates a new LogOutEvent
 */
public abstract class LogOutEvent extends BaseEvent {
	/**
	 * @param name
	 * 
	 * Creates a new Login event,
	 * if name is null, it will be like anonymus function
	 */
	public LogOutEvent(String name) {
		if (name != null) {
			this.name = name;
		}
	}
}
