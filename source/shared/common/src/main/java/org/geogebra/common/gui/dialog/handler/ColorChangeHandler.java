package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.awt.GColor;

/**
 * Color change handler.
 */
public interface ColorChangeHandler {
	void onColorChange(GColor color);

	void onAlphaChange();

	void onClearBackground();

	void onForegroundSelected();

	void onBackgroundSelected();

	void onBarSelected();
}
