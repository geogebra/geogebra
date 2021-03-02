package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertArrayEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.junit.Before;
import org.junit.Test;

public class PointFromSequenceTest extends BaseUnitTest {

	private Kernel kernel;

	@Before
	public void setUp() throws Exception {
		kernel = getApp().getKernel();
	}

	@Test
	public void testPointSequence() {
		GeoPoint point = add("Point(Sequence(Sequence((i, j), i, 1, 10, 0.1)"
				+ ", j, 1, 10, 0.1))");
		point.set(newPoint(2.9, 7.6));
		GeoPoint expected = newPoint(3, 7.6);
		point.set(expected);
		kernel.updateConstruction();
		assertArrayEquals(expected.getCoords().get(), point.getCoords().get(),
				Kernel.MAX_PRECISION);
	}
	private GeoPoint newPoint(double x, double y) {
		return new GeoPoint(kernel.getConstruction(), x, y, 1);
	}
}
