package org.geogebra.web.full.gui.menu;

/**
 * Listener of the MenuViewController.
 */
public interface MenuViewListener {

	/**
	 * Fired when the menu is opened.
	 */
	void onMenuOpened();

	/**
	 * Fired when the menu is closed.
	 */
	void onMenuClosed();
}
