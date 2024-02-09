package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.Scrollable;
import org.geogebra.common.util.shape.Rectangle;

/**
 * A utility class designed to adjust the viewport if a cell, row, or column that is not fully
 * visible is clicked
 */
public class ViewportAdjuster {

	private final TableLayout layout;
	private final Scrollable scrollable;
	private final static int SCROLL_INCREMENT = 2;

	/**
	 * @param layout TableLayout
	 * @param scrollable Scrollable
	 */
	public ViewportAdjuster(TableLayout layout, Scrollable scrollable) {
		this.layout = layout;
		this.scrollable = scrollable;
	}

	/**
	 * @return Scrollable
	 */
	public Scrollable getScrollable() {
		return this.scrollable;
	}

	/**
	 * @param column Row index
	 * @param viewport Viewport
	 * @return True if the viewport was adjusted horizontally, false else
	 */
	public boolean adjustViewportHorizontallyIfNeeded(int column, Rectangle viewport) {
		int scrollAmount = 0;
		if (shouldAdjustViewportHorizontallyRightwards(column, viewport)) {
			scrollAmount = (int) (layout.getX(column + 1) - viewport.getMinX()
					+ layout.getRowHeaderWidth() - viewport.getWidth()
					+ scrollable.getScrollBarWidth() + SCROLL_INCREMENT);
		} else if (shouldAdjustViewportHorizontallyLeftwards(column, viewport)) {
			scrollAmount = (int) -(viewport.getMinX() - layout.getX(column));
		}
		if (scrollAmount != 0) {
			scrollable.setHorizontalScrollPosition(
					scrollable.getHorizontalScrollPosition() + scrollAmount);
			return true;
		}
		return false;
	}

	/**
	 * @param row Row index
	 * @param viewport Viewport
	 * @return True if the viewport was adjusted vertically, false else
	 */
	public boolean adjustViewportVerticallyIfNeeded(int row, Rectangle viewport) {
		int scrollAmount = 0;
		if (shouldAdjustViewportVerticallyDownwards(row, viewport)) {
			scrollAmount = (int) (layout.getY(row + 1) - viewport.getMinY()
					+ layout.getColumnHeaderHeight() - viewport.getHeight()
					+ scrollable.getScrollBarWidth() + SCROLL_INCREMENT);
		} else if (shouldAdjustViewportVerticallyUpwards(row, viewport)) {
			scrollAmount = (int) -(viewport.getMinY() - layout.getY(row));
		}
		if (scrollAmount != 0) {
			scrollable.setVerticalScrollPosition(
					scrollable.getVerticalScrollPosition() + scrollAmount);
			return true;
		}
		return false;
	}

	private boolean shouldAdjustViewportHorizontallyRightwards(int column, Rectangle viewport) {
		return layout.getX(column + 1) - viewport.getMinX() + layout.getRowHeaderWidth()
				> viewport.getWidth() - scrollable.getScrollBarWidth();
	}

	private boolean shouldAdjustViewportHorizontallyLeftwards(int column, Rectangle viewport) {
		return layout.getX(column) < viewport.getMinX();
	}

	private boolean shouldAdjustViewportVerticallyDownwards(int row, Rectangle viewport) {
		return layout.getY(row + 1) - viewport.getMinY() + layout.getColumnHeaderHeight()
				> viewport.getHeight() - scrollable.getScrollBarWidth();
	}

	private boolean shouldAdjustViewportVerticallyUpwards(int row, Rectangle viewport) {
		return layout.getY(row) < viewport.getMinY();
	}
}
