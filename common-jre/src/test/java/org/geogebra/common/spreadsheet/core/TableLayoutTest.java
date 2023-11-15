package org.geogebra.common.spreadsheet.core;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.util.shape.Rectangle;
import org.junit.Test;

public class TableLayoutTest {
	TableLayout layout = new TableLayout(5, 5, 20, 40);

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
		int colHeader = TableLayout.DEFAUL_CELL_HEIGHT;
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
}
