package geogebra.common.plugin;

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
	public void sendEvent(Event evt);

	/**
	 * This method is called every time we have a new construction. The event
	 * listener should get rid of all the scripts
	 * 
	 * At the moment this is triggered when View.clearView() is called
	 * TODO check that this only really happens when a new file is created or opened.
	 */
	public void reset();
}
