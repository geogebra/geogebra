package org.geogebra.common.gui.slider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.util.slider.SliderBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.error.ErrorHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SliderBuilderTest extends BaseUnitTest {

	private SliderBuilder sliderBuilder;
	private Construction construction;

	@Before
	public void setupSliderBuilder() {
		Kernel kernel = getApp().getKernel();
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
		assertNotNull(sliderBuilder.create());
		assertTrue(isSliderInConstructionList());
	}

	private boolean isSliderInConstructionList() {
		ConstructionElement slider = construction.getConstructionElement(0);
		return slider instanceof GeoNumeric;
	}

	@Test
	public void createWithEmptyInput() {
		sliderBuilder.withMin("");
		assertNull(sliderBuilder.create());
		assertFalse(isSliderInConstructionList());
	}

	@Test
	public void testSuppressLabelFlagAfterCreated() {
		assertFalse(construction.isSuppressLabelsActive());
		createSimple();
		assertFalse(construction.isSuppressLabelsActive());
	}

	@Test
	public void testSuppressLabelFlagAfterEmptyInput() {
		assertFalse(construction.isSuppressLabelsActive());
		createWithEmptyInput();
		assertFalse(construction.isSuppressLabelsActive());
	}
}