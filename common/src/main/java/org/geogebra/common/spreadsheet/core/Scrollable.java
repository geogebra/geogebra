package org.geogebra.common.spreadsheet.core;

public interface Scrollable {

	void setVerticalScrollPosition(int position);

	void setHorizontalScrollPosition(int position);

	/**
	 * Retrieves the width of the scrollbar used for dragging content with the left mouse button
	 * @return The width of the scrollbar
	 */
	int getScrollBarWidth();
}