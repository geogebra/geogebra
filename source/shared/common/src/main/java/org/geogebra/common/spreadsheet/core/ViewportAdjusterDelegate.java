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