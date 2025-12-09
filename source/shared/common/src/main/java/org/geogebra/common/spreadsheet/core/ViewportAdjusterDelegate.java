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

package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.gui.EdgeInsets;
import org.geogebra.common.util.shape.Size;

/**
 * An abstraction for the scrollable container hosting the spreadsheet.
 */
public interface ViewportAdjusterDelegate {

	/**
	 * Retrieves the width of the scrollbar used for dragging content with the left mouse button
	 * @return The width of the scrollbar
	 */
	double getScrollBarWidth();

	/**
	 * Update size of the scrollable area.
	 * @param size total size of the scrollable content
	 */
	void updateScrollableContentSize(Size size);

	/**
	 * Make x/y the top left corner of the visible area.
	 * @param x Horizontal scroll offset.
	 * @param y Vertical scroll offset.
	 */
	void setScrollPosition(double x, double y);

	/**
	 * Get the insets for the viewport that is safe to interact with
	 * (is not overlapped by any other UI element)
	 * @return viewport insets
	 */
	default EdgeInsets getViewportInsets() {
		return new EdgeInsets();
	}
}