package org.geogebra.common.move.views;

/**
 * @author gabor must be implemented by views, that added to the view list of
 *         BaseView
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
