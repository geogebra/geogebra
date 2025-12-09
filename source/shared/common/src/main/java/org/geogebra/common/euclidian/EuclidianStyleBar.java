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

package org.geogebra.common.euclidian;

import org.geogebra.common.annotation.MissingDoc;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * Style bar for Euclidian view
 */
public interface EuclidianStyleBar {

	// /** tooltip x location for buttons */
	// public static final int TOOLTIP_LOCATION_X = 0;
	// /** tooltip y location for buttons */
	// public static final int TOOLTIP_LOCATION_Y = -25;

	/**
	 * @param mode
	 *            euclidian view mode
	 */
	void setMode(int mode);

	/**
	 * Update tooltips
	 */
	void setLabels();

	/**
	 * Restore default properties
	 */
	void restoreDefaultGeo();

	/**
	 * Updates the state of the stylebar buttons and the defaultGeo field.
	 */
	void updateStyleBar();

	/**
	 * Update capture button
	 * 
	 * @param mode
	 *            euclidian view mode
	 */
	void updateButtonPointCapture(int mode);

	/**
	 * update the style bar if the geo is part of the active geo list
	 * 
	 * @param geo
	 *            geo
	 */
	void updateVisualStyle(GeoElement geo);

	/**
	 * @return index of selected point capturing mode
	 */
	int getPointCaptureSelectedIndex();

	@MissingDoc
	void updateGUI();

	@MissingDoc
	void hidePopups();

	/**
	 * reset "first paint", so that on first paint the GUI will be updated
	 */
	void resetFirstPaint();

	/**
	 * Re-initialize.
	 */
	void reinit();

	/**
	 * Show or hide the style bar.
	 * @param visible whether it should be visible
	 */
	void setVisible(boolean visible);

	/**
	 * @return whether the style bar is visible.
	 */
	boolean isVisible();
}
