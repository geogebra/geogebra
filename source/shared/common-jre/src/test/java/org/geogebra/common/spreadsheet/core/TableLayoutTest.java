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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.util.MouseCursor;
import org.geogebra.common.util.shape.Point;
import org.geogebra.common.util.shape.Rectangle;
import org.geogebra.common.util.shape.Size;
import org.junit.Test;

public class TableLayoutTest {
	private final int rowHeight = 20;
	private final int columnWidth = 40;

	TableLayout layout = new TableLayout(5, 5, rowHeight, columnWidth);

	@Test
	public void testFindColumn() {
		assertThat(layout.findColumn(-5), equalTo(-1));
		double rowHeaderWidth = layout.getRowHeaderWidth();
		assertThat(layout.findColumn(rowHeaderWidth - 1), equalTo(-1));
		assertThat(layout.findColumn(rowHeaderWidth + 1), equalTo(0));
		assertThat(layout.findColumn(rowHeaderWidth + 40), equalTo(1));
		assertThat(layout.findColumn(rowHeaderWidth + 41), equalTo(1));
		assertThat(layout.findColumn(rowHeaderWidth + 80), equalTo(2));
		assertThat(layout.findColumn(10000), equalTo(5));
	}

	@Test
	public void testFindRow() {
		assertThat(layout.findRow(-5), equalTo(-1));
		double columnHeaderHeight = layout.getColumnHeaderHeight();
		assertThat(layout.findRow(columnHeaderHeight - 1), equalTo(-1));
		assertThat(layout.findRow(columnHeaderHeight + 1), equalTo(0));
		assertThat(layout.findRow(columnHeaderHeight + 20), equalTo(1));
		assertThat(layout.findRow(columnHeaderHeight + 21), equalTo(1));
		assertThat(layout.findRow(columnHeaderHeight + 40), equalTo(2));
		assertThat(layout.findRow(10000), equalTo(5));
	}

	@Test
	public void testVisiblePortion() {
		TableLayout.Portion pt = layout.getLayoutIntersecting(
				new Rectangle(0, 100, 0, 100));
		assertThat(pt.fromRow, equalTo(0));
		assertThat(pt.fromColumn, equalTo(0));
		assertThat(pt.toRow, equalTo(4));
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
		viewportOrigin = new Point(rowHeaderWidth / 2 + columnWidth, 0);
		assertThat(layout.getResizeAction(mouseX, columnHeaderHeight / 2,
						new Rectangle(viewportOrigin, viewportSize)).cursor,
				equalTo(MouseCursor.DEFAULT));
	}
}
