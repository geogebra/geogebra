package org.geogebra.web.html5.gui;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * Handler for TouchStart / MouseDown events.
 */
public interface FastClickHandler extends EventHandler {
	/**
	 * Called when a FastClickEvent is fired.
	 * 
	 * @param source
	 *            the widget that fired the event.
	 */
	void onClick(Widget source);
}
