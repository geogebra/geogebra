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
	 * This method is called every time the construction is cleared. This is a
	 * cue to the event listener to get rid of the scripts attached to
	 * particular objects.
	 * 
	 * TODO check how to make this work
	 */
	// public void clearObjects();
}
