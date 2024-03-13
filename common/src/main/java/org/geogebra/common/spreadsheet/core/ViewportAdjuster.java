package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.shape.Rectangle;

/**
 * A utility class designed to adjust the viewport if a cell, row, or column that is not fully
 * visible is clicked
 */
public class ViewportAdjuster {

	private final TableLayout layout;
	private final ViewportAdjustmentHandler viewportAdjustmentHandler;
	private final static int SCROLL_INCREMENT = 2;

	/**
	 * @param layout TableLayout
	 * @param viewportAdjustmentHandler ViewportAdjustmentHandler
	 */
	public ViewportAdjuster(TableLayout layout,
			ViewportAdjustmentHandler viewportAdjustmentHandler) {
		this.layout = layout;
		this.viewportAdjustmentHandler = viewportAdjustmentHandler;
	}

	/**
	 * @param row Column index
	 * @param column Row index
	 * @param viewport Viewport
	 * @return True if the viewport was adjusted horizontally, false else
	 */
	public boolean adjustViewportIfNeeded(int row, int column, Rectangle viewport) {
		double scrollAmountX = 0;
		if (shouldAdjustViewportHorizontallyRightwards(column, viewport)) {
			scrollAmountX = Math.ceil(layout.getX(column + 1) - viewport.getMinX()
					+ layout.getRowHeaderWidth() - viewport.getWidth()
					+ viewportAdjustmentHandler.getScrollBarWidth() + SCROLL_INCREMENT);
		} else if (shouldAdjustViewportHorizontallyLeftwards(column, viewport)) {
			scrollAmountX = -Math.floor(viewport.getMinX() - layout.getX(column));
		}

		double scrollAmountY = 0;
		if (shouldAdjustViewportVerticallyDownwards(row, viewport)) {
			scrollAmountY = Math.ceil(layout.getY(row + 1) - viewport.getMinY()
					+ layout.getColumnHeaderHeight() - viewport.getHeight()
					+ viewportAdjustmentHandler.getScrollBarWidth() + SCROLL_INCREMENT);
		} else if (shouldAdjustViewportVerticallyUpwards(row, viewport)) {
			scrollAmountY = -Math.floor(viewport.getMinY() - layout.getY(row));
		}

		if (scrollAmountX != 0 || scrollAmountY != 0) {
			viewportAdjustmentHandler.setScrollPosition(
					(int) (viewport.getMinX() + scrollAmountX),
					(int) (viewport.getMinY() + scrollAmountY));
			return true;
		}
		return false;
	}

	private boolean shouldAdjustViewportHorizontallyRightwards(int column, Rectangle viewport) {
		if (!isValidColumnIndex(column + 1) || cellIsWiderThanViewport(column, viewport)) {
			return false;
		}
		return layout.getX(column + 1) - viewport.getMinX() + layout.getRowHeaderWidth()
				> viewport.getWidth() - viewportAdjustmentHandler.getScrollBarWidth();
	}

	private boolean shouldAdjustViewportHorizontallyLeftwards(int column, Rectangle viewport) {
		return isValidColumnIndex(column) && layout.getX(column) < viewport.getMinX();
	}

	private boolean shouldAdjustViewportVerticallyDownwards(int row, Rectangle viewport) {
		if (!isValidRowIndex(row + 1) || cellIsHigherThanViewport(row, viewport)) {
			return false;
		}
		return layout.getY(row + 1) - viewport.getMinY() + layout.getColumnHeaderHeight()
				> viewport.getHeight() - viewportAdjustmentHandler.getScrollBarWidth();
	}

	private boolean shouldAdjustViewportVerticallyUpwards(int row, Rectangle viewport) {
		return isValidRowIndex(row) && layout.getY(row) < viewport.getMinY();
	}

	private boolean isValidColumnIndex(int column) {
		return column >= 0 && column < layout.numberOfColumns();
	}

	private boolean isValidRowIndex(int row) {
		return row >= 0 && row < layout.numberOfRows();
	}

	private boolean cellIsWiderThanViewport(int column, Rectangle viewport) {
		return isValidColumnIndex(column)
				&& layout.getWidth(column) + layout.getRowHeaderWidth() > viewport.getWidth();
	}

	private boolean cellIsHigherThanViewport(int row, Rectangle viewport) {
		return isValidRowIndex(row)
				&& layout.getHeight(row) + layout.getColumnHeaderHeight() > viewport.getHeight();
	}
}
