package geogebra.touch.gui.algebra.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link FastClickEvent} events.
 */
public interface FastClickHandler extends EventHandler {
	/**
	 * Called when a FastClickEvent is fired.
	 */
	void onClick();
}
