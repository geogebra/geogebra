package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SliderTest extends BaseUnitTest {

	private EvalInfo info;

	@Before
	public void setUp() {
		info = EvalInfoFactory.getEvalInfoForAV(getApp(), true);
	}

	@Test
	public void setShowExtendedAV() {
		GeoNumeric slider = add("a = 1", info);
		slider.initAlgebraSlider();
		slider.setShowExtendedAV(false);
		assertThat(slider.showInEuclidianView(), is(true));
	}
}