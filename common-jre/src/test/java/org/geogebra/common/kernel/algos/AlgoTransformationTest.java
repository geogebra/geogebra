package org.geogebra.common.kernel.algos;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.geos.GeoLine;
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

	@Test
	public void dilateOfDegenerate() {
		String[] pts = createTransformedDegenerate("Dilate(%,2,(1,1))");
		assertArrayEquals(new String[]{"(-1, -1)", "(-1, 3)"}, pts);
	}

	@Test
	public void translateOfDegenerate() {
		String[] pts = createTransformedDegenerate("Translate(%,(1,1))");
		assertArrayEquals(new String[]{"(1, 1)", "(1, 3)"}, pts);
	}

	@Test
	public void reflectOfDegenerate() {
		String[] pts = createTransformedDegenerate("Reflect(%,(1,1))");
		assertArrayEquals(new String[]{"(2, 2)", "(2, 0)"}, pts);
	}

	@Test
	public void reflectInLineOfDegenerate() {
		String[] pts = createTransformedDegenerate("Reflect(%,x=y)");
		assertArrayEquals(new String[]{"(0, 0)", "(2, 0)"}, pts);
	}

	@Test
	public void circleInversionOfSegment() {
		add("A=(2,2)");
		// use point on path to check TRAC-3781
		add("B=Point(Segment((2,0),(3,0)))");
		GeoConicPart arc = add("Reflect(Segment(A,B),x^2+y^2=1)");
		assertThat(arc, hasValue("0.39"));
		assertFalse(getDirection(arc));
		// segment mapped to quarter-circle
		assertEquals(0.0, arc.getParameterStart(), 0.01);
		assertEquals(Math.PI / 2, arc.getParameterEnd(),  0.01);
	}

	@Test
	public void circleInversionOfRay() {
		add("A=(2,2)");
		add("B=Point(Segment((2,0),(3,0)))");
		// with +0 we force the algo to transform ray directly
		GeoConicPart arc = add("Reflect(Ray(B+0,(2,2)),x^2+y^2=1)");
		assertThat(arc, hasValue("0.79"));
		// ray mapped to semicircle
		assertTrue(getDirection(arc));
		assertEquals(0, arc.getParameterStart(), 0.01);
		assertEquals(Math.PI, arc.getParameterEnd(), 0.01);
		// when using point names we expand the definition, but should get equal result
		GeoConicPart arc2 = add("Reflect(Ray(B,A),x^2+y^2=1)");
		assertEquals(arc2.getDefinition(StringTemplate.defaultTemplate),
				"CircumcircularArc(B', A', Reflect((∞, ∞), x² + y² = 1))");
		assertTrue(getDirection(arc));
		assertEquals(0, arc2.getParameterStart(), 0.01);
		assertEquals(Math.PI, arc2.getParameterEnd(), 0.01);
	}

	private String[] createTransformedDegenerate(String s) {
		add("a=CircumcircularArc((0,0),(0,1),(0,2))");
		GeoConicPart transformed = add(s.replace("%", "a"));
		GeoLine line = transformed.getLines()[0];
		return new String[]{ line.getStartPoint().toValueString(StringTemplate.editTemplate),
				line.getEndPoint().toValueString(StringTemplate.editTemplate)};
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
