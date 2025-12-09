/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.gui.dialog.handler;

import org.geogebra.common.annotation.MissingDoc;
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

	@MissingDoc
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
