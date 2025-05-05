package org.geogebra.web.full.gui.util;

/**
 * Handles both input and change events from a slider.
 */
public interface SliderEventHandler {
	/**
	 * Called for each change event (drag stop, arrow pressed).
	 */
	void onValueChange();

	/**
	 * Called for every input event (while dragging).
	 */
	void onSliderInput();
}
