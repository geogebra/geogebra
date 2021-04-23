package org.geogebra.suite;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.SpecialPointsManager;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;

public class SpecialPointsTest extends BaseSuiteTest {

	@Test
	public void testRemovableDiscontinuity() {
		SpecialPointsManager manager = getApp().getSpecialPointsManager();

		GeoElement element = add("f(x)=(3-x)/(2x^2-6x)");
		manager.updateSpecialPoints(element);
		List<GeoElement> specialPoints = manager.getSelectedPreviewPoints();
		assertThat(specialPoints,
				CoreMatchers.<GeoElement>hasItem(hasToString("null = (3, -0.17)")));
	}

	@Test
	public void testRemovableDiscontinuityPointStyle() {
		SpecialPointsManager manager = getApp().getSpecialPointsManager();

		GeoElement element = add("f(x)=(3-x)/(2x^2-6x)");
		manager.updateSpecialPoints(element);
		List<GeoElement> specialPoints = manager.getSelectedPreviewPoints();
		assertThat(specialPoints,
				CoreMatchers.hasItem(
						Matchers.<GeoElement>hasProperty("pointStyle", is(
								EuclidianStyleConstants.POINT_STYLE_CIRCLE))));
	}

	@Test
	public void testRegressionApps2777() {
		GeoElement element = add("f(x)=tan^(-1)((1+x)/(1-x))",
				EvalInfoFactory.getEvalInfoForAV(getApp()));
		SpecialPointsManager manager = getApp().getSpecialPointsManager();
		manager.updateSpecialPoints(element);
	}
}
