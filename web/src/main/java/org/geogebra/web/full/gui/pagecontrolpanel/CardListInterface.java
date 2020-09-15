package org.geogebra.web.full.gui.pagecontrolpanel;

import com.google.gwt.user.client.ui.ScrollPanel;

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

	/** Hide the scrollbar */
	void hideScrollbar();

	/** Show the scrollbar if it was visible before. */
	void restoreScrollbar();

	/**
	 * Scroll the panel by diff.
	 * 
	 * @param diff
	 *            to scroll by.
	 * @return if scroll has reached the end.
	 */
	boolean scrollBy(int diff);

	/**
	 * 
	 * @return the vertical scroll position of the cards.
	 */
	int getVerticalScrollPosition();

	/**
	 * 
	 * @return the height of the visible area of the scroll panel.
	 */
	int getScrollParentHeight();

	/**
	 * @return the scrollPanel
	 */
	ScrollPanel getScrollPanel();

	/**
	 * resets the page control panel
	 */
	void reset();

	/**
	 * opens the page control panel
	 */
	void open();
}