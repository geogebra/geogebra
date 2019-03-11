package org.geogebra.common.gui.util.slider;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App;
import org.geogebra.common.spy.AppSpy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SliderBuilderTest {

	private App app;
	private Construction construction;
	private SliderBuilder sliderBuilder;

	public SliderBuilderTest() {
		app = new AppSpy();
		construction = app.getKernel().getConstruction();
	}

	@Before
	public void setUp() {
		sliderBuilder = new SliderBuilder(app);
		sliderBuilder.withMin("-5").withMax("5").withStep("1").withLocation(0, 0);
	}


	@Test
	public void createSimple() {
		boolean wasSuppressedLabelsActive = construction.isSuppressLabelsActive();
		sliderBuilder.create();
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