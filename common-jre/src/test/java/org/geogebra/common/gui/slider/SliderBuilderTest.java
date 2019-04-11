package org.geogebra.common.gui.slider;

import static org.mockito.Mockito.mock;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.util.slider.SliderBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.error.ErrorHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SliderBuilderTest {

	private App app;
	private SliderBuilder sliderBuilder;
	private Construction construction;

	public SliderBuilderTest() {
		app = BaseUnitTest.createAppCommon();
	}

	@Before
	public void setUp() {
		Kernel kernel = app.getKernel();
		construction = kernel.getConstruction();
		AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
		ErrorHandler errorHandler = mock(ErrorHandler.class);

		sliderBuilder = new SliderBuilder(algebraProcessor, errorHandler);
		sliderBuilder.withMin("-5").withMax("5").withStep("1").withLocation(0, 0);
	}

	@After
	public void tearDown() {
		construction.clearConstruction();
	}

	@Test
	public void createSimple() {
		Assert.assertNotNull(sliderBuilder.create());
		Assert.assertTrue(isSliderInConstructionList());
	}

	private boolean isSliderInConstructionList() {
		ConstructionElement slider = construction.getConstructionElement(0);
		return slider instanceof GeoNumeric;
	}

	@Test
	public void createWithEmptyInput() {
		sliderBuilder.withMin("");
		Assert.assertNull(sliderBuilder.create());
		Assert.assertFalse(isSliderInConstructionList());
	}

	@Test
	public void testSuppressLabelFlagAfterCreated() {
		boolean wasSuppressedLabelsActive = construction.isSuppressLabelsActive();
		createSimple();
		boolean isFlagSetBack =
				(wasSuppressedLabelsActive == construction.isSuppressLabelsActive());
		Assert.assertTrue(isFlagSetBack);
	}

	@Test
	public void testSuppressLabelFlagAfterEmptyInput() {
		boolean wasSuppressedLabelsActive = construction.isSuppressLabelsActive();
		createWithEmptyInput();
		boolean isFlagSetBack =
				(wasSuppressedLabelsActive == construction.isSuppressLabelsActive());
		Assert.assertTrue(isFlagSetBack);
	}
}