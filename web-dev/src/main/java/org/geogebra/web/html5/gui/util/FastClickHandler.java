package org.geogebra.web.html5.gui.util;

import org.gwtproject.user.client.ui.Widget;

/**
 * Handler for TouchStart / MouseDown events.
 */
public interface FastClickHandler {
	/**
	 * Called when a FastClickEvent is fired.
	 * 
	 * @param source
	 *            the widget that fired the event.
	 */
	void onClick(Widget source);
}
