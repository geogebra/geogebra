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
 
package org.geogebra.common.gui.view.table;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.geogebra.common.gui.view.table.dimensions.TableValuesViewDimensions;
import org.geogebra.common.gui.view.table.dimensions.TextSizeMeasurer;
import org.geogebra.common.io.FactoryProviderCommon;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class TableValuesViewDimensionsTest extends MockedTableValuesUnitTest {

	private TableValuesViewDimensions dimensions;
	private TextSizeMeasurer measurer;

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
		mockModelCell(0, 0, "1");
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
		mockModelCell(0, 0, "10");
		mockModelCell(1, 0, "11");

		// Dimensions initial state
		dimensions.notifyColumnAdded(model, null, 0);
		// Warm up the cache
		dimensions.getColumnWidth(0);

		mockRowCount(1);
		dimensions.notifyRowsRemoved(model, 1, 1);
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
		mockModelCell(0, 0, "10");
		mockModelCell(1, 0, "11");

		// Dimensions initial state
		dimensions.notifyColumnAdded(model, null, 0);
		// Warm up the cache
		dimensions.getColumnWidth(0);

		mockRowCount(2);
		dimensions.notifyRowsAdded(model, 1, 1);

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

	private void mockMeasureWidth(String content, int width) {
		when(measurer.getWidth(content)).thenReturn(width);
	}
}
