package org.geogebra.common.gui.view.table;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.awt.font.GTextLayout;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.gui.view.table.dimensions.TableValuesViewDimensions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TableValuesViewDimensionsTest extends BaseUnitTest {

	private GRectangle2D rectangle;
	private SimpleTableValuesModel model;
	private TableValuesViewDimensions dimensions;

	/**
	 * Sets up the test suite.
	 */
	@Before
	public void setupTest() {
		GFontRenderContext context = Mockito.mock(GFontRenderContext.class);
		GTextLayout layout = Mockito.mock(GTextLayout.class);
		AwtFactory factory = Mockito.mock(AwtFactory.class);
		rectangle = Mockito.mock(GRectangle2D.class);
		model = new SimpleTableValuesModel(getKernel());
		dimensions = new TableValuesViewDimensions(model, factory, context);
		model.registerListener(dimensions);
		Mockito.when(layout.getBounds()).thenReturn(rectangle);
		Mockito.when(factory.newTextLayout(Mockito.anyString(), Mockito.any(), Mockito.any()))
				.thenReturn(layout);
	}

	@Test
	public void testMinWidth() {
		model.setValues(new double[]{1.0});
		Mockito.when(rectangle.getWidth()).thenReturn(TableValuesViewDimensions.MIN_WIDTH / 10.0);
		int width = dimensions.getColumnWidth(0);
		Assert.assertEquals(TableValuesViewDimensions.MIN_WIDTH, width);
	}

	@Test
	public void testMaxWidth() {
		model.setValues(new double[]{1.0});
		Mockito.when(rectangle.getWidth()).thenReturn(TableValuesViewDimensions.MAX_WIDTH * 10.0);
		int width = dimensions.getColumnWidth(0);
		Assert.assertEquals(TableValuesViewDimensions.MAX_WIDTH, width);
	}

	@Test
	public void testWidthBetweenMinAndMax() {
		model.setValues(new double[]{1.0});
		double middleWidth =
				(TableValuesViewDimensions.MAX_WIDTH + TableValuesViewDimensions.MIN_WIDTH) / 2.0;
		Mockito.when(rectangle.getWidth()).thenReturn(middleWidth);
		int width = dimensions.getColumnWidth(0);
		Assert.assertEquals(middleWidth, width, 0.001);
	}
}
