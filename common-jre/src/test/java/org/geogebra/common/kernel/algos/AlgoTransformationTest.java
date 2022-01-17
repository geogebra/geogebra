package org.geogebra.common.kernel.algos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.junit.Before;
import org.junit.Test;

public class AlgoTransformationTest extends BaseUnitTest {

	@Before
	public void setupArcs() {
		assertClockwise(add("clockwise=CircumcircularArc((0,0),(0,1),(1,0))"));
		assertCounterclockwise(add("counterclockwise=CircumcircularArc((1,0),(0,1),(0,0))"));
	}

	@Test
	public void translationShouldKeepArcOrientation() {
		assertClockwise(add("Translate(clockwise, (2,0))"));
		assertCounterclockwise(add("Translate(counterclockwise, (2,0))"));
	}

	@Test
	public void dilationShouldKeepArcOrientation() {
		assertClockwise(add("Dilate(clockwise, 2, (2,0))"));
		assertCounterclockwise(add("Dilate(counterclockwise, 2, (2,0))"));
	}

	@Test
	public void mirrorShouldFlipArcOrientation() {
		assertCounterclockwise(add("Reflect(clockwise, x=0)"));
		assertClockwise(add("Reflect(counterclockwise, x=0)"));
	}

	private void assertClockwise(GeoConicPart arc) {
		assertFalse("Arc should be clockwise " + arc, getDirection(arc));
		assertEquals(Math.PI * 3 / 2, arc.getParameterExtent(), 1E-4);
	}

	private boolean getDirection(GeoConicPart arc) {
		return arc.positiveOrientation()
				^ determinant(arc.getAffineTransform()) < 0;
	}

	private double determinant(GAffineTransform t) {
		return t.getScaleX() * t.getScaleY() - t.getShearX() * t.getShearY();
	}

	private void assertCounterclockwise(GeoConicPart arc) {
		assertTrue("Arc should be counterclockwise " + arc, getDirection(arc));
		assertEquals(Math.PI * 3 / 2, arc.getParameterExtent(), 1E-4);
	}
}
