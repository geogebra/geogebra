package org.geogebra.common.gui.view.table;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.dimensions.TableValuesViewDimensions;
import org.geogebra.common.gui.view.table.dimensions.TextSizeMeasurer;
import org.geogebra.common.io.FactoryProviderCommon;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class TableValuesViewDimensionsTest extends BaseUnitTest {

	private TableValuesModel model;
	private TableValuesViewDimensions dimensions;
	private TextSizeMeasurer measurer;

	/**
	 * Sets up the test suite.
	 */
	@Before
	public void setupTest() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		measurer = Mockito.mock(TextSizeMeasurer.class);
		model = Mockito.mock(TableValuesModel.class);
		dimensions = new TableValuesViewDimensions(model, measurer);
	}

	private void mockModelWithSingleValue() {
		mockRowCount(1);
		mockColumnCount(1);
		mockModelValue(0, 0, "1");
		dimensions.notifyColumnAdded(model, null, 0);
	}

	@Test
	public void testMinWidth() {
		mockModelWithSingleValue();
		when(measurer.getWidth(Mockito.anyString()))
				.thenReturn(TableValuesViewDimensions.MIN_COLUMN_WIDTH / 10);
		int width = dimensions.getColumnWidth(0);
		assertEquals(TableValuesViewDimensions.MIN_COLUMN_WIDTH, width);
	}

	@Test
	public void testMaxWidth() {
		mockModelWithSingleValue();
		when(measurer.getWidth(Mockito.anyString()))
				.thenReturn(TableValuesViewDimensions.MAX_COLUMN_WIDTH * 10);
		int width = dimensions.getColumnWidth(0);
		assertEquals(TableValuesViewDimensions.MAX_COLUMN_WIDTH, width);
	}

	@Test
	public void testWidthBetweenMinAndMax() {
		mockModelWithSingleValue();
		int contentWidth = TableValuesViewDimensions.MIN_COLUMN_WIDTH;
		when(measurer.getWidth(Mockito.anyString()))
				.thenReturn(contentWidth);
		int width = dimensions.getColumnWidth(0);
		assertEquals(getClampedWidthWithMargins(contentWidth), width);
	}

	@Test
	public void testCacheRecalculatesWidthWhenRowRemoved() {
		int longContentWidth = TableValuesViewDimensions.MIN_COLUMN_WIDTH + 20;
		mockMeasureWidth("10", TableValuesViewDimensions.MIN_COLUMN_WIDTH);
		mockMeasureWidth("11", longContentWidth);
		mockRowCount(2);
		mockColumnCount(1);
		mockModelValue(0, 0, "10");
		mockModelValue(1, 0, "11");

		// Dimensions initial state
		dimensions.notifyColumnAdded(model, null, 0);
		// Warm up the cache
		dimensions.getColumnWidth(0);

		mockRowCount(1);
		dimensions.notifyRowRemoved(model, 1);
		assertEquals(
				getClampedWidthWithMargins(TableValuesViewDimensions.MIN_COLUMN_WIDTH),
				dimensions.getColumnWidth(0));
	}

	@Test
	public void testCacheRecalculatesWidthWhenRowAdded() {
		int longContentWidth = TableValuesViewDimensions.MIN_COLUMN_WIDTH + 20;
		mockMeasureWidth("10", TableValuesViewDimensions.MIN_COLUMN_WIDTH);
		mockMeasureWidth("11", longContentWidth);
		mockRowCount(1);
		mockColumnCount(1);
		mockModelValue(0, 0, "10");
		mockModelValue(1, 0, "11");

		// Dimensions initial state
		dimensions.notifyColumnAdded(model, null, 0);
		// Warm up the cache
		dimensions.getColumnWidth(0);

		mockRowCount(2);
		dimensions.notifyRowAdded(model, 1);

		assertEquals(getClampedWidthWithMargins(longContentWidth), dimensions.getColumnWidth(0));
	}

	private int getClampedWidthWithMargins(int width) {
		int widthWithMargins = width
				+ TableValuesViewDimensions.CELL_LEFT_MARGIN
				+ TableValuesViewDimensions.CELL_RIGHT_MARGIN;
		return Math.min(
				Math.max(widthWithMargins, TableValuesViewDimensions.MIN_COLUMN_WIDTH),
				TableValuesViewDimensions.MAX_COLUMN_WIDTH);
	}

	private void mockModelValue(int row, int column, String value) {
		when(model.getCellAt(row, column)).thenReturn(new TableValuesCell(value, false));
	}

	private void mockRowCount(int row) {
		when(model.getRowCount()).thenReturn(row);
	}

	private void mockColumnCount(int column) {
		when(model.getColumnCount()).thenReturn(column);
	}

	private void mockMeasureWidth(String content, int width) {
		when(measurer.getWidth(content)).thenReturn(width);
	}
}
