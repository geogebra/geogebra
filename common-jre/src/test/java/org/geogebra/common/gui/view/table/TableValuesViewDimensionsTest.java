package org.geogebra.common.gui.view.table;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.table.dimensions.TableValuesViewDimensions;
import org.geogebra.common.gui.view.table.dimensions.TextSizeMeasurer;
import org.geogebra.common.io.FactoryProviderCommon;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class TableValuesViewDimensionsTest extends BaseUnitTest {

	private SimpleTableValuesModel model;
	private TableValuesViewDimensions dimensions;
	private TextSizeMeasurer measurer;

	/**
	 * Sets up the test suite.
	 */
	@Before
	public void setupTest() {
		FactoryProvider.setInstance(new FactoryProviderCommon());
		measurer = Mockito.mock(TextSizeMeasurer.class);
		model = new SimpleTableValuesModel(getKernel());
		dimensions = new TableValuesViewDimensions(model, measurer);
		model.registerListener(dimensions);
	}

	@Test
	public void testMinWidth() {
		model.setValues(new double[]{1.0});
		Mockito.when(measurer.getWidth(Mockito.anyString()))
				.thenReturn(TableValuesViewDimensions.MIN_COLUMN_WIDTH / 10);
		int width = dimensions.getColumnWidth(0);
		Assert.assertEquals(TableValuesViewDimensions.MIN_COLUMN_WIDTH, width);
	}

	@Test
	public void testMaxWidth() {
		model.setValues(new double[]{1.0});
		Mockito.when(measurer.getWidth(Mockito.anyString()))
				.thenReturn(TableValuesViewDimensions.MAX_COLUMN_WIDTH * 10);
		int width = dimensions.getColumnWidth(0);
		Assert.assertEquals(TableValuesViewDimensions.MAX_COLUMN_WIDTH, width);
	}

	@Test
	public void testWidthBetweenMinAndMax() {
		model.setValues(new double[]{1.0});
		int contentWidth = TableValuesViewDimensions.MIN_COLUMN_WIDTH;
		Mockito.when(measurer.getWidth(Mockito.anyString()))
				.thenReturn(contentWidth);
		int width = dimensions.getColumnWidth(0);
		Assert.assertEquals(contentWidth + TableValuesViewDimensions.CELL_RIGHT_MARGIN
				+ TableValuesViewDimensions.CELL_LEFT_MARGIN, width);
	}
}
