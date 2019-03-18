package org.geogebra.common.gui.slider;

import static org.mockito.Mockito.mock;

import org.geogebra.common.gui.util.slider.SliderBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCommon;
import org.geogebra.common.main.error.ErrorHandler;
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
		app = new AppCommon();
	}

	@Before
	public void setUp() {
		Kernel kernel = app.getKernel();
		construction = kernel.getConstruction();
		AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
		ErrorHandler errorHandler = mock(ErrorHandler.class);

		//sliderBuilder = new SliderBuilder(algebraProcessor, errorHandler);
		//sliderBuilder.withMin("-5").withMax("5").withStep("1").withLocation(0, 0);
	}

	@Test
	public void createSimple() {
		boolean wasSuppressedLabelsActive = construction.isSuppressLabelsActive();
		//assert sliderBuilder.create() != null;
		assert isSliderInConstructionList();
		assert wasSuppressedLabelsActive == construction.isSuppressLabelsActive();
	}

	private boolean isSliderInConstructionList() {
		ConstructionElement slider = construction.getConstructionElement(0);
		return slider instanceof GeoNumeric;
	}

	@Test
	public void createWithEmptyInput() {
		boolean wasSuppressLabelsActive = construction.isSuppressLabelsActive();
		//liderBuilder.withMin("");
		//try {
		//	sliderBuilder.create();
		//	Assert.fail("wrong");
		//} catch (Exception ignored) {
		//}
		assert !isSliderInConstructionList();
		assert wasSuppressLabelsActive == construction.isSuppressLabelsActive();
	}
}