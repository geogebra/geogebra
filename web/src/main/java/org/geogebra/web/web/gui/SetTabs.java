package org.geogebra.web.web.gui;

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
	public void setTabIndex(int index);

	/**
	 * Clears tab indexes for the widget.
	 */
	public void clearTabIndex();

}
