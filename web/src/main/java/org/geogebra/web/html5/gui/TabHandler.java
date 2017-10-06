package org.geogebra.web.html5.gui;

import com.google.gwt.user.client.ui.Widget;

/**
 * Tab key handler for FastButton
 * 
 * @author laszlo
 *
 */
public interface TabHandler {
	/**
	 * Called on tab key down.
	 * 
	 * @param source
	 *            The sender widget
	 * 
	 * @param shiftDown
	 *            true that Shift is down.
	 * @return true if event sould be killed.
	 */
	boolean onTab(Widget source, boolean shiftDown);
}
