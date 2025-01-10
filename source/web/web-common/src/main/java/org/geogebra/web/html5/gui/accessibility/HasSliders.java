package org.geogebra.web.html5.gui.accessibility;

/**
 * UI element with one or more sliders
 */
public interface HasSliders {

	/**
	 * @param index slider index
	 * @param value slider value
	 */
	void onValueChange(int index, double value);

}
