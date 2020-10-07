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
	 * Scroll the panel by diff.
	 * 
	 * @param diff
	 *            to scroll by.
	 */
	void scrollBy(int diff);

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

	void updateContentPanelHeight();
}
