package org.geogebra.suite;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;

import java.util.List;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.SpecialPointsManager;
import org.hamcrest.CoreMatchers;
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
}
