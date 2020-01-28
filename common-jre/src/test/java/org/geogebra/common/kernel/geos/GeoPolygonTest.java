package org.geogebra.common.kernel.geos;

import org.geogebra.common.BaseUnitTest;
import org.junit.Assert;
import org.junit.Test;

public class GeoPolygonTest extends BaseUnitTest {

	@Test
	public void testMaskProperties() {
		add("A=(-1,1)");
		add("B=(-1,-1)");
		add("C=(1,-1)");
		add("D=(1,1)");
		GeoPolygon polygon = add("Polygon(A,B,C,D)");

		Assert.assertTrue(polygon.isFillable());
		Assert.assertTrue(polygon.isTraceable());
		Assert.assertTrue(polygon.showLineProperties());

		polygon.setIsMask(true);
		Assert.assertFalse(polygon.isFillable());
		Assert.assertFalse(polygon.isTraceable());
		Assert.assertFalse(polygon.showLineProperties());
	}
}
