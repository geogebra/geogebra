package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class AlgoIntersectFunctionLineNewtonTest extends BaseUnitTest {

	@Test
	@Issue("APPS-5509")
	public void highPrecisionRounding() {
		getApp().setRounding("13");
		GeoPoint pt = add("Intersect(sin(x deg),y=1)");
		assertThat(pt, hasValue("(90, 1)"));
	}
}
