package org.geogebra.common.spreadsheet.core;

import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;

/**
 * A utility class designed to adjust the spreadsheet's viewport if needed<br/>
 * This can happen in two different scenarios:
 * <li>A cell, row, or column that is not fully visible is clicked</li>
 * <li>The paste selection is dragged to the top / right / bottom / left edge</li>
 */
// TODO testing: This class contains a lot of tricky logic, so it should be directly unit-tested
//  (not just indirectly via SpreadsheetController).
public final class ViewportAdjuster {

	private final TableLayout layout;
	private final ViewportAdjusterDelegate viewportAdjusterDelegate;
	private final static int SCROLL_INCREMENT = 2;
	private final static int SCROLL_AMOUNT_FOR_PASTE_SELECTION = 7;

	/**
	 * @param layout TableLayout
	 * @param viewportAdjusterDelegate ViewportAdjustmentHandler
	 */
	ViewportAdjuster(TableLayout layout,
			ViewportAdjusterDelegate viewportAdjusterDelegate) {
		this.layout = layout;
		this.viewportAdjusterDelegate = viewportAdjusterDelegate;
	}

	/**
	 * If the right edge of the cell is to the right of the viewport, scroll right.
	 * If the left edge of the cell is to the left of the viewport
	 *  - if we already scrolled right (cell bigger than viewport), we cancel the scroll
	 *  - otherwise scroll left
	 * @param row Row index
	 * @param column Column index
	 * @param viewport Viewport
	 * @return Updated viewport
	 */
	Rectangle adjustViewportIfNeeded(int row, int column, Rectangle viewport) {
		double scrollAmountX = getScrollAmountX(column, viewport);
		double scrollAmountY = getScrollAmountY(row, viewport);

		if (scrollAmountX != 0 || scrollAmountY != 0) {
			viewportAdjusterDelegate.setScrollPosition(
					(int) (viewport.getMinX() + scrollAmountX),
					(int) (viewport.getMinY() + scrollAmountY));
			return viewport.translatedBy(scrollAmountX, scrollAmountY);
		}
		return viewport;
	}

	/**
	 * Starts scrolling the view when dragging happens  at the top / right / bottom / left
	 * edge of the viewport
	 * @param x Horizontal pointer position
	 * @param y Vertical pointer position
	 * @param viewport Viewport
	 * @param extendVertically True if the paste selection is extended vertically, false else
	 * (horizontally)
	 * @return new viewport
	 */
	Rectangle scrollForDrag(int x, int y, Rectangle viewport,
			boolean extendVertically) {
		int viewportWidth = (int) viewport.getWidth();
		int viewportHeight = (int) viewport.getHeight();
		int scrollAmountX = 0;
		int scrollAmountY = 0;

		if (extendVertically) {
			scrollAmountY = getVerticalScrollAmountForDrag(viewportHeight, y);
		} else {
			scrollAmountX = getHorizontalScrollAmountForDrag(viewportWidth, x);
		}

		if (scrollAmountX == 0 && scrollAmountY == 0) {
			return viewport;
		}
		double targetX = viewport.getMinX() + scrollAmountX;
		double targetY = viewport.getMinY() + scrollAmountY;
		if (targetX < 0
				|| targetX > layout.getTotalWidth()
				|| targetY < 0
				|| targetY > layout.getTotalHeight()) {
			return viewport;
		}

		viewportAdjusterDelegate.setScrollPosition((int) targetX, (int) targetY);
		return viewport.translatedBy(scrollAmountX, scrollAmountY);
	}

	private double getScrollAmountX(int column, Rectangle viewport) {
		double scrollAmountX = 0;
		boolean scrolledRight = false;
		if (shouldAdjustViewportHorizontallyRightwards(column, viewport)) {
			scrollAmountX = Math.ceil(layout.getX(column + 1) - viewport.getMinX()
					+ layout.getRowHeaderWidth() - viewport.getWidth()
					+ viewportAdjusterDelegate.getScrollBarWidth() + SCROLL_INCREMENT);
			scrolledRight = true;
		}
		if (shouldAdjustViewportHorizontallyLeftwards(column, viewport)) {
			scrollAmountX = scrolledRight ? 0
					: -Math.floor(viewport.getMinX() - layout.getX(column));
		}
		return scrollAmountX;
	}

	private double getScrollAmountY(int row, Rectangle viewport) {
		double scrollAmountY = 0;
		boolean scrolledDown = false;
		if (shouldAdjustViewportVerticallyDownwards(row, viewport)) {
			scrollAmountY = Math.ceil(layout.getY(row + 1) - viewport.getMinY()
					+ layout.getColumnHeaderHeight() - viewport.getHeight()
					+ viewportAdjusterDelegate.getScrollBarWidth() + SCROLL_INCREMENT
					+ viewportAdjusterDelegate.getViewportInsets().getBottom());
			scrolledDown = true;
		}
		if (shouldAdjustViewportVerticallyUpwards(row, viewport)) {
			scrollAmountY = scrolledDown ? 0 : -Math.floor(viewport.getMinY() - layout.getY(row));
		}
		return scrollAmountY;
	}

	private boolean shouldAdjustViewportHorizontallyRightwards(int column, Rectangle viewport) {
		if (!isValidColumnIndex(column)) {
			return false;
		}
		return layout.getX(column + 1) - viewport.getMinX() + layout.getRowHeaderWidth()
				> viewport.getWidth() - viewportAdjusterDelegate.getScrollBarWidth();
	}

	private boolean shouldAdjustViewportHorizontallyLeftwards(int column, Rectangle viewport) {
		return isValidColumnIndex(column) && layout.getX(column) < viewport.getMinX();
	}

	private boolean shouldAdjustViewportVerticallyDownwards(int row, Rectangle viewport) {
		if (!isValidRowIndex(row)) {
			return false;
		}
		return layout.getY(row + 1) - viewport.getMinY() + layout.getColumnHeaderHeight()
				> viewport.getHeight() - viewportAdjusterDelegate.getScrollBarWidth()
				- viewportAdjusterDelegate.getViewportInsets().getBottom();
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

	public void updateScrollPaneSize(Size size) {
		viewportAdjusterDelegate.updateScrollableContentSize(size);
	}

	private int getVerticalScrollAmountForDrag(int viewportHeight, int y) {
		if (viewportHeight - y < viewportHeight / 10) {
			return SCROLL_AMOUNT_FOR_PASTE_SELECTION;
		} else if (y < viewportHeight / 10) {
			return -SCROLL_AMOUNT_FOR_PASTE_SELECTION;
		}
		return 0;
	}

	private int getHorizontalScrollAmountForDrag(int viewportWidth, int x) {
		if (viewportWidth - x < viewportWidth / 10) {
			return SCROLL_AMOUNT_FOR_PASTE_SELECTION;
		} else if (x < viewportWidth / 10) {
			return -SCROLL_AMOUNT_FOR_PASTE_SELECTION;
		}
		return 0;
	}
}
