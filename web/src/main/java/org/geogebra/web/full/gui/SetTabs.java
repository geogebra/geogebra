package org.geogebra.web.full.gui;

/**
 * Manages the proper tab indexes for the widget and its children.
 * 
 * @author Laszlo Gal
 *
 */
public interface SetTabs {

	/**
	 * Sets the proper tab indexes for the widget.
	 * 
	 * @param index
	 *            top element tab index
	 */
	void setTabIndex(int index);

	/**
	 * Clears tab indexes for the widget.
	 */
	void clearTabIndex();

}
