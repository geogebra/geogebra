package geogebra.common.move.events;

/**
 * @author gabor
 *	used to attach native events from Desktop and Web or Touch
 */
public interface NativeEventAttacher {
	
	/**
	 * @param type the type of the event
	 * @param eventPool Attaches the given eventPool to native events
	 */
	public void attach(String type, BaseEventPool eventPool);

}
