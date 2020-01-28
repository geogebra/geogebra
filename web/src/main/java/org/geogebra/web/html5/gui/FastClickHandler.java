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

	// This is an unfortunate hack to avoid changing too many
	// public classes that implement FastClickHandler
	// If you happen upon this in a Java8 world, maybe rethink it, using lambdas
	interface Typed extends FastClickHandler {

		/**
		 * Called when a FastClickEvent is fired
		 * @param eventType name of the event, e.g. 'mouseup' or 'touchend'
		 */
		void onClick(String eventType);
	}
}
