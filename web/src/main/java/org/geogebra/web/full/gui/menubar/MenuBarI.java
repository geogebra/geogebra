package org.geogebra.web.full.gui.menubar;

import org.geogebra.web.html5.gui.menu.AriaMenuItem;

/**
 * Shared interface for export and download menu
 *
 */
public interface MenuBarI {

	/**
	 * Hide this after item was clicked (if necessary)
	 */
	void hide();

	/**
	 * @return menu item to be added
	 */
	AriaMenuItem addItem(AriaMenuItem item);

}
