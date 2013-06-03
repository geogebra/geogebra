package geogebra.common.move.events;

/**
 * @author gabor
 *	base of all Events
 */
public abstract class BaseEvent {
	
	/**
	 * Needed for identify the event, otherwise it
	 * it will be like anonymous functions, can't be removed individually
	 */
	protected String name = null;
	
	/**
	 * @return the name of the event, or null
	 *  needed for identify it
	 */
	
	public String getName() {
		return name;
	}
	
	
	
	/**
	 * contains the event handling code
	 */
	public abstract void trigger();
}
