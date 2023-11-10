package org.geogebra.common.gui.dialog.options.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoLine;
import org.junit.Test;

public class LineStyleModelTest extends BaseUnitTest {

	@Test
	public void sliderSnapShouldNotChangeValue() {
		LineStyleModel model = new LineStyleModel(getApp());
		GeoLine line = add("x=y");
		model.setGeos(new Object[]{line});
		for (int pct = 0; pct <= 100; pct +=5) {
			model.applyOpacityPercentage(pct);
			assertThat(model.getOpacityPercentage(), equalTo(pct));
		}
	}

}
