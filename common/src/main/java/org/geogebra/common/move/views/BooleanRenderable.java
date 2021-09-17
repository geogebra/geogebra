package org.geogebra.common.move.views;

/**
 * Interface for views listening to boolean events
 * @author gabor
 */
public interface BooleanRenderable {
	/**
	 * renders the given view
	 * 
	 * @param b
	 *            true for online, false for offline
	 */
	public void render(boolean b);
}
