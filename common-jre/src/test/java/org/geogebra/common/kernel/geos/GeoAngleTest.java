package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GeoAngleTest extends BaseUnitTest {

	@Test
	public void testCopy() {
		GeoAngle angle = addAvInput("90°");
		angle.setDrawable(true, false);
		GeoAngle copy = angle.copy();
		assertThat(copy.isDrawable, is(true));
	}
}