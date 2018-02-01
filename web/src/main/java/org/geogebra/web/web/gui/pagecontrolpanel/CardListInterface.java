package org.geogebra.web.web.gui.pagecontrolpanel;

/**
 * Interface for updating card container visuals.
 * 
 * @author laszlo
 *
 */
public interface CardListInterface {
	/** rebuilds container */
	void update();

	/**
	 * Sets insert indicator to the right place
	 * 
	 * @param targetIdx
	 *            The index of the card, divider will be inserted before.
	 */
	void setDivider(int targetIdx);
}