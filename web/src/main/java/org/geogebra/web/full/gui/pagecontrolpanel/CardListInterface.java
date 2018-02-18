package org.geogebra.web.full.gui.pagecontrolpanel;

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
	void insertDivider(int targetIdx);

	/**
	 * removes divider
	 */
	void removeDivider();

	void hideScrollbar();

	void restoreScrollbar();

}