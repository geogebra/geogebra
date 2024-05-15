package org.geogebra.common.spreadsheet.core;

public interface ViewportAdjustmentHandler {

	void setScrollPosition(int x, int y);

	/**
	 * Retrieves the width of the scrollbar used for dragging content with the left mouse button
	 * @return The width of the scrollbar
	 */
	int getScrollBarWidth();

	/**
	 * Update size of the scrollable area.
	 */
	void updateScrollPanelSize();
}