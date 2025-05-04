package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.awt.GColor;

/**
 * Color change handler.
 */
public interface ColorChangeHandler {
	/**
	 * Called when color changed.
	 * @param color new color
	 */
	void onColorChange(GColor color);

	/**
	 * Called when opacity changed.
	 */
	void onAlphaChange();

	void onClearBackground();

	/**
	 * Called when selector switched to foreground mode.
	 */
	void onForegroundSelected();

	/**
	 * Called when selector switched to background mode.
	 */
	void onBackgroundSelected();

	/**
	 * Called when bar chart's bar is selected.
	 */
	void onBarSelected();
}
