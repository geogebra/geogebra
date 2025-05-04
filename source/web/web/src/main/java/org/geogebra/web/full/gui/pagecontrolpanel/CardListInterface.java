package org.geogebra.web.full.gui.pagecontrolpanel;

/**
 * Interface for updating card container visuals.
 * 
 * @author laszlo
 *
 */
public interface CardListInterface {
	/** rebuilds the container */
	void update();

	/**
	 * Scroll the panel by diff.
	 * 
	 * @param diff
	 *            to scroll by.
	 */
	void scrollBy(int diff);

	/**
	 * resets the page control panel
	 */
	void reset();

	/**
	 * opens the page control panel
	 */
	void open();

	/**
	 * Update content height.
	 */
	void updateContentPanelHeight();

	/**
	 * Update card indices.
	 * @param index first index to update
	 */
	void updateIndexes(int index);
}

