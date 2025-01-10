package org.geogebra.common.plugin;

/**
 * @see EventDispatcher
 * @author arno
 * 
 */
public interface EventListener {

	/**
	 * This method is called by the event dispatcher every time an event is
	 * triggered
	 * 
	 * @param evt
	 *            the event
	 */
	void sendEvent(Event evt);

	/**
	 * This method is called every time we have a new construction. The event
	 * listener should get rid of all the scripts
	 * 
	 * <p>At the moment this is triggered when View.clearView() is called TODO
	 * check that this only really happens when a new file is created or opened.
	 */
	default void reset() {
		// not needed in most cases
	}
}
