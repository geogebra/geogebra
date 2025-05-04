package org.geogebra.common.euclidian.draw;

/**
 * Facade for DrawVector.
 * TODO remove?
 */
public interface DrawableVisibility {
	/**
	 * @param visible visibility flag
	 */
	void setVisible(boolean visible);

	/**
	 * @return whether it's visible
	 */
	boolean isVisible();
}
