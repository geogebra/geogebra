package org.geogebra.common.kernel.geos;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class GeoAngleTest extends BaseUnitTest {

	@Test
	public void testCopy() {
		GeoAngle angle = addAvInput("90°");
		angle.setDrawable(true, false);
		GeoAngle copy = angle.copy();
		assertThat(copy.isDrawable, is(true));
	}

	@Test
	public void testSetAllVisualPropertiesExceptEuclidianVisible() {
		GeoAngle hidden = addAvInput("90°");
		hidden.setDrawable(false, false);
		GeoAngle visible = addAvInput("90°");
		visible.setDrawable(true, false);
		visible.setAllVisualPropertiesExceptEuclidianVisible(
				hidden, false, true);
		assertThat(visible.isDrawable, is(true));
	}
}