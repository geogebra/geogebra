package org.geogebra.suite;

import static org.geogebra.test.OrderingComparison.lessThan;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.geogebra.common.gui.view.algebra.EvalInfoFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.SpecialPointsManager;
import org.geogebra.common.plugin.EuclidianStyleConstants;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class SpecialPointsTest extends BaseSuiteTest {

	@Before
	public void setRounding() {
		getKernel().setPrintDecimals(2);
	}

	@Test
	public void testRemovableDiscontinuity1() {
		SpecialPointsManager manager = getApp().getSpecialPointsManager();

		GeoElement element = add("f(x)=(3-x)/(2x^2-6x)");
		manager.updateSpecialPoints(element);
		List<GeoElement> specialPoints = manager.getSelectedPreviewPoints();
		assertThat(specialPoints,
				CoreMatchers.hasItem(hasToString("null = (3, -0.17)")));
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

	/**
	 * Avoid suite crash - APPS-5273
	 */
	@Test
	public void testRemovableDiscontinuity2() {
		long time = System.currentTimeMillis();
		GeoElement element =
				add("f(x)=nroot(((8-2 x)/(x^(2)-5 x+6)),3)-((ln(4-2 x))/(nroot(x^(2)-x,6)))");
		SpecialPointsManager manager = getApp().getSpecialPointsManager();
		manager.updateSpecialPoints(element);
		assertThat(System.currentTimeMillis() - time, lessThan(3000L));
	}

	@Test
	public void testRemovableDiscontinuity3() {
		SpecialPointsManager manager = getApp().getSpecialPointsManager();
		GeoElement element = add("f(x)=((x^2-4)/(x-2))");
		manager.updateSpecialPoints(element);
		List<GeoElement> specialPoints = manager.getSelectedPreviewPoints();
		assertThat(specialPoints,
				CoreMatchers.hasItem(hasToString("null = (2, 4)")));
	}

	@Test
	public void testRemovableDiscontinuity4() {
		SpecialPointsManager manager = getApp().getSpecialPointsManager();
		GeoElement element = add("f(x)=((sin(x))/(x))");
		manager.updateSpecialPoints(element);
		List<GeoElement> specialPoints = manager.getSelectedPreviewPoints();
		assertThat(specialPoints,
				CoreMatchers.hasItem(hasToString("null = (0, 1)")));
	}

	@Test
	public void testNoRemovableDiscontinuity1() {
		SpecialPointsManager manager = getApp().getSpecialPointsManager();
		GeoElement element = add("f(x)=((abs(x-3))/(x-3))");
		manager.updateSpecialPoints(element);
		List<GeoElement> specialPoints = manager.getSelectedPreviewPoints();
		assertThat(specialPoints, CoreMatchers.everyItem(anyOf(
				hasValue("(0, -1)"),
				hasValue("(?, ?)"))
		));
	}
}
