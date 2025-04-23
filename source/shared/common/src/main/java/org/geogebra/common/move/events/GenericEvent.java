package org.geogebra.common.move.events;

/**
 * Event that can be fired on given type of targets.
 * @param <T> receiving type
 */
public interface GenericEvent<T> {

	/**
	 * Fire the event.
	 * @param target target
	 */
	void fire(T target);
}
