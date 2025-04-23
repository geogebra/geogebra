package org.geogebra.common.move.events;

import org.geogebra.common.move.views.EventRenderable;

/**
 * Base of all Events
 * 
 * @author gabor
 */
public abstract class BaseEvent implements GenericEvent<EventRenderable> {

	/**
	 * Needed for identify the event, otherwise it it will be like anonymous
	 * functions, can't be removed individually
	 */
	protected final String name;

	/**
	 * @param name
	 *            event name
	 */
	public BaseEvent(String name) {
		this.name = name;
	}

	/**
	 * @return the name of the event, or null needed for identify it
	 */

	public String getName() {
		return name;
	}

	@Override
	public void fire(EventRenderable target) {
		target.renderEvent(this);
	}

}
