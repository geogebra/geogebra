package geogebra.common.move.ggtapi.events;

import geogebra.common.move.events.BaseEvent;

/**
 * @author gabor
 * 	Event for login operations
 *
 */
public abstract class LoginEvent extends BaseEvent {
	/**
	 * @param name
	 * 
	 * Creates a new Login event,
	 * if name is null, it will be like anonymus function
	 */
	public LoginEvent(String name) {
		if (name != null) {
			this.name = name;
		}
	}
}
