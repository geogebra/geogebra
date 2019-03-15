package org.geogebra.common.gui.util.slider;

import org.geogebra.common.spy.SpyProvider;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.error.ErrorHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SliderBuilderTest {

	private SpyProvider spyProvider;
	private SliderBuilder sliderBuilder;
	private Construction construction;

	public SliderBuilderTest() {
		spyProvider = new SpyProvider();
	}

	@Before
	public void setUp() {
		construction = spyProvider.getConstruction();
		AlgebraProcessor algebraProcessor = spyProvider.getAlgebraProcessor();
		ErrorHandler errorHandler = spyProvider.getErrorHandler();

		sliderBuilder = new SliderBuilder(algebraProcessor, errorHandler);
		sliderBuilder.withMin("-5").withMax("5").withStep("1").withLocation(0, 0);
	}

	@Test
	public void createSimple() {
		boolean wasSuppressedLabelsActive = construction.isSuppressLabelsActive();
		assert sliderBuilder.create() != null;
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
		sliderBuilder.withMin("");
		try {
			sliderBuilder.create();
			assert false;
		} catch (Throwable ignored) {
		}
		assert !isSliderInConstructionList();
		assert wasSuppressLabelsActive == construction.isSuppressLabelsActive();
	}
}