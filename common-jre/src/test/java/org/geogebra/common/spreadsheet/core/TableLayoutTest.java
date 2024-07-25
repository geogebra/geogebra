package org.geogebra.common.spreadsheet.core;

import static org.geogebra.common.spreadsheet.core.TableLayout.DEFAULT_CELL_HEIGHT;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Point;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;
import org.junit.Test;

public class TableLayoutTest {
	private final int rowHeight = 20;
	private final int columWidth = 40;

	TableLayout layout = new TableLayout(5, 5, rowHeight, columWidth);

	@Test
	public void testFindColumn() {
		assertThat(layout.findColumn(-5), equalTo(-1));
		int rowHeader = TableLayout.DEFAULT_ROW_HEADER_WIDTH;
		assertThat(layout.findColumn(rowHeader - 1), equalTo(-1));
		assertThat(layout.findColumn(rowHeader + 1), equalTo(0));
		assertThat(layout.findColumn(rowHeader + 40), equalTo(1));
		assertThat(layout.findColumn(rowHeader + 41), equalTo(1));
		assertThat(layout.findColumn(rowHeader + 80), equalTo(2));
		assertThat(layout.findColumn(10000), equalTo(5));
	}

	@Test
	public void testFindRow() {
		assertThat(layout.findRow(-5), equalTo(-1));
		int colHeader = DEFAULT_CELL_HEIGHT;
		assertThat(layout.findRow(colHeader - 1), equalTo(-1));
		assertThat(layout.findRow(colHeader + 1), equalTo(0));
		assertThat(layout.findRow(colHeader + 20), equalTo(1));
		assertThat(layout.findRow(colHeader + 21), equalTo(1));
		assertThat(layout.findRow(colHeader + 40), equalTo(2));
		assertThat(layout.findRow(10000), equalTo(5));
	}

	@Test
	public void testVisiblePortion() {
		TableLayout.Portion pt = layout.getLayoutIntersecting(
				new Rectangle(0, 100, 0, 100));
		assertThat(pt.fromRow, equalTo(0));
		assertThat(pt.fromColumn, equalTo(0));
		assertThat(pt.toRow, equalTo(3));
		assertThat(pt.toColumn, equalTo(1));

		pt = layout.getLayoutIntersecting(
				new Rectangle(10, 100, 10, 100));
		assertThat(pt.fromRow, equalTo(0));
		assertThat(pt.fromColumn, equalTo(0));

		pt = layout.getLayoutIntersecting(
				new Rectangle(41, 100, 21, 100));
		assertThat(pt.fromRow, equalTo(1));
		assertThat(pt.fromColumn, equalTo(1));
	}

	@Test
	public void testCursorInSelectAllCorner() {
		// note: mouse coordinates are window coordinates / are relative to the viewport
		double columnHeaderHeight = layout.getColumnHeaderHeight();
		double rowHeaderWidth = layout.getRowHeaderWidth();
		double mouseX = 0.5 * rowHeaderWidth; // in the middle of the corner
		Size viewportSize = new Size(100, 100);

		// in the center of the corner, viewport at 0
		Point viewportOrigin = new Point(0, 0);
		assertThat(layout.getResizeAction(mouseX, columnHeaderHeight / 2,
						new Rectangle(viewportOrigin, viewportSize)).cursor,
				equalTo(MouseCursor.DEFAULT));

		// in the center of the corner, viewport scrolled vertically by columnHeaderHeight / 2
		viewportOrigin = new Point(0, 0.5 * columnHeaderHeight);
		assertThat(layout.getResizeAction(mouseX, columnHeaderHeight / 2,
						new Rectangle(viewportOrigin, viewportSize)).cursor,
				equalTo(MouseCursor.DEFAULT));

		// in the center of the corner, viewport scrolled vertically by columnHeaderHeight / 2
		// + height of first row
		viewportOrigin = new Point(0, 0.5 * columnHeaderHeight + rowHeight);
		assertThat(layout.getResizeAction(mouseX, columnHeaderHeight / 2,
						new Rectangle(viewportOrigin, viewportSize)).cursor,
				equalTo(MouseCursor.DEFAULT));

		// at the bottom edge of the corner, viewport at 0
		viewportOrigin = new Point(0, 0);
		assertThat(layout.getResizeAction(mouseX, columnHeaderHeight,
						new Rectangle(viewportOrigin, viewportSize)).cursor,
				equalTo(MouseCursor.DEFAULT));

		// at the bottom edge of first visible row, viewport scrolled vertically by
		// columnHeaderHeight
		viewportOrigin = new Point(0, columnHeaderHeight);
		assertThat(layout.getResizeAction(mouseX, columnHeaderHeight + rowHeight,
						new Rectangle(viewportOrigin, viewportSize)).cursor,
				equalTo(MouseCursor.RESIZE_Y));

		// in the center of the corner, viewport scrolled horizontally by rowHeaderWidth / 2
		// + width of first column
		viewportOrigin = new Point(rowHeaderWidth / 2 + columWidth, 0);
		assertThat(layout.getResizeAction(mouseX, columnHeaderHeight / 2,
						new Rectangle(viewportOrigin, viewportSize)).cursor,
				equalTo(MouseCursor.DEFAULT));
	}
}
