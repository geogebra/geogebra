package org.geogebra.web.full.gui.util;

/**
 * Handles both input and change events from a slider.
 */
public interface SliderEventHandler {
	void onValueChange();

	void onSliderInput();
}
