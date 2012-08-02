package geogebra.web.html5;

/**
 * @author gabor
 * 
 * Popups should implement this interface, from those only one exist
 * in a given time.
 *
 */
public interface AttachedToDOM {
	
	/**
	 * removes the given popup from DOM
	 */
	public void removeFromDOM();

}
