package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertArrayEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.junit.Before;
import org.junit.Test;

public class PointFromSequenceTest extends BaseUnitTest {

	private Kernel kernel;
	private GeoPoint point;

	@Before
	public void setUp() throws Exception {
		kernel = getApp().getKernel();
	}

	@Test
	public void testPointSequence() {
		point = add("Point(Sequence(Sequence((i, j), i, 1, 10, 0.1)"
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

	@Test
	public void testPointOnSimpleSequencePath() {
		simplePathWithStep(0.1);
		point.set(newPoint(1.51, 1.51));
		kernel.updateConstruction();
		pointShouldBe(1.5, 1.5);
	}

	private void pointShouldBe(double x, double y) {
		GeoPoint expected = newPoint(x, y);
		assertArrayEquals(expected.getCoords().get(), point.getCoords().get(),
				Kernel.MAX_PRECISION);

	}

	private void simplePathWithStep(double step) {
		point = add("Point(Sequence((i, i), i, 1, 10, " + step + "))");
	}

	@Test
	public void testPointOnSimpleSequencePathStep9() {
		simplePathWithStep(0.9);
		point.set(newPoint(1.7, 1.7));
		kernel.updateConstruction();
		pointShouldBe(1.9, 1.9);
	}

	@Test
	public void testPointOnSimpleSequencePathStep3Floor() {
		simplePathWithStep(0.3);
		point.set(newPoint(3.2, 3.2));
		kernel.updateConstruction();
		pointShouldBe(3.1, 3.1);
	}

	@Test
	public void testPointOnSimpleSequencePathStep3() {
		simplePathWithStep(0.3);
		point.set(newPoint(3.25, 3.25));
		kernel.updateConstruction();
		pointShouldBe(3.4, 3.4);
	}

	@Test
	public void testPointOnSimpleSequencePathStep3Precise() {
		simplePathWithStep(0.3);
		point.set(newPoint(3.4, 3.4));
		kernel.updateConstruction();
		pointShouldBe(3.4, 3.4);
	}

}
