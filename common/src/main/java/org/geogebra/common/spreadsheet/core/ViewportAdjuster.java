package org.geogebra.common.spreadsheet.core;

import java.util.function.BiFunction;

import org.geogebra.common.util.Scrollable;
import org.geogebra.common.util.shape.Rectangle;

/**
 * A utility class designed to adjust the viewport if a cell that is not fully visible is clicked
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
	 * @return A function that takes the column index (Integer) and the viewport (Rectangle)
	 * as inputs. If the viewport should be adjusted horizontally, because the clicked cell is not
	 * fully visible, this method returns true. Otherwise, it returns false.
	 */
	public BiFunction<Integer, Rectangle, Boolean> adjustViewportHorizontallyIfNeeded() {
		return (column, viewport) -> {
			int scrollAmount = 0;
			if (shouldAdjustViewportHorizontallyRightwards(column, viewport)) {
				scrollAmount = (int) (layout.getX(column + 1) - viewport.getMinX()
						+ layout.getRowHeaderWidth() - scrollable.getOffsetWidth()
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
		};
	}

	/**
	 * @return A function that takes the row index (Integer) and the viewport (Rectangle)
	 * as inputs. If the viewport should be adjusted vertically, because the clicked cell is not
	 * fully visible, this method returns true. Otherwise, it returns false.
	 */
	public BiFunction<Integer, Rectangle, Boolean> adjustViewportVerticallyIfNeeded() {
		return (row, viewport) -> {
			int scrollAmount = 0;
			if (shouldAdjustViewportVerticallyDownwards(row, viewport)) {
				scrollAmount = (int) (layout.getY(row + 1) - viewport.getMinY()
						+ layout.getColumnHeaderHeight() - scrollable.getOffsetHeight()
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
		};
	}

	private boolean shouldAdjustViewportHorizontallyRightwards(int column, Rectangle viewport) {
		return layout.getX(column + 1) - viewport.getMinX() + layout.getRowHeaderWidth()
				> scrollable.getOffsetWidth() - scrollable.getScrollBarWidth();
	}

	private boolean shouldAdjustViewportHorizontallyLeftwards(int column, Rectangle viewport) {
		return layout.getX(column) < viewport.getMinX();
	}

	private boolean shouldAdjustViewportVerticallyDownwards(int row, Rectangle viewport) {
		return layout.getY(row + 1) - viewport.getMinY() + layout.getColumnHeaderHeight()
				> scrollable.getOffsetHeight() - scrollable.getScrollBarWidth();
	}

	private boolean shouldAdjustViewportVerticallyUpwards(int row, Rectangle viewport) {
		return layout.getY(row) < viewport.getMinY();
	}
}
