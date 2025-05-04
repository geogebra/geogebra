package org.geogebra.web.full.gui.util;

/**
 * Popup menu handler.
 */
public interface PopupMenuHandler {

	/**
	 * Called when item is selected in a popup menu.
	 * @param selectedIndex selected index
	 */
	void fireActionPerformed(int selectedIndex);

}
