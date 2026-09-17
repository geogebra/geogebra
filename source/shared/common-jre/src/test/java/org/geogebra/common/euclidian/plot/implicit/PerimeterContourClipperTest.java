/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.common.euclidian.plot.implicit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.arithmetic.bernstein.BernsteinPolynomial2D;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.implicit.GeoImplicitCurve;
import org.geogebra.common.util.debug.Log;
import org.junit.jupiter.api.Test;

class PerimeterContourClipperTest extends BaseContourTestSetup {
	private PerimeterContourClipper clipper;
	private EuclidianViewBounds bounds;

	@Test
	void testConnect() {
		GeoElement cassini = addCassini(2.9, 2.98);
		bounds = newBounds(
				-14.900000000000029,
				9.220000000000018,
				-24.190000000000026,
				1.1100000000000014,
				1206,
				1265);

		ContourInfo info = builder.withImplicitCurve(cassini).withBounds(bounds).build();
		clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(bounds);
	}

	@Test
	void unclippedClosedContourShouldProduceClosedFragment() {
		bounds = newBounds(-2, 2, -2, 2, 800, 800);
		GeoElement curve = evaluateGeoElement("x^4 + y^4 = 1");

		ContourInfo info = builder.withImplicitCurve(curve).withBounds(bounds).build();

		clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(bounds);

		List<ClippedFragment> fragments = clipper.getFragments();
		assertEquals(1, fragments.size());
		ClippedFragment fragment = fragments.get(0);
		assertTrue(fragment.start() == null || fragment.start().getEdge() == null);
		assertTrue(fragment.end() == null || fragment.end().getEdge() == null);
	}

	@Test
	void clippedContourShouldProduceOpenFragmentWithEndpointMetadata() {
		GeoElement cassini = addCassini(2.9, 2.98);
		bounds = newBounds(
				-14.900000000000029,
				9.220000000000018,
				-24.190000000000026,
				1.1100000000000014,
				1206,
				1265);

		ContourInfo info = builder.withImplicitCurve(cassini).withBounds(bounds).build();
		clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(bounds);

		ClippedFragment fragment =
				clipper.getFragments().stream().filter(f -> !f.closed()).findFirst().orElseThrow();

		assertNotNull(fragment.start());
		assertNotNull(fragment.end());
		assertTrue(fragment.start().getSourceSegmentIndex() >= 0);
		assertTrue(fragment.end().getSourceSegmentIndex() >= 0);
		assertEquals(fragment.sourceContourId(), fragment.start().getSourceContourId());
		assertEquals(fragment.sourceContourId(), fragment.end().getSourceContourId());
	}

	@Test
	void openFragmentsShouldNotContainSyntheticClosingSegment() {
		GeoElement cassini = addCassini(2.9, 2.98);
		bounds = newBounds(
				-14.900000000000029,
				9.220000000000018,
				-24.190000000000026,
				1.1100000000000014,
				1206,
				1265);

		ContourInfo info = builder.withImplicitCurve(cassini).withBounds(bounds).build();
		clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(bounds);

		for (ClippedFragment fragment : clipper.getFragments()) {
			if (fragment.closed()) {
				continue;
			}
			List<MyPoint> points = fragment.points();
			MyPoint first = points.get(0);
			MyPoint last = points.get(points.size() - 1);
			assertFalse(samePoint(first, last));
		}
	}

	@Test
	void cornerTouchingFragmentsShouldKeepDistinctOpenEndpoints() {
		bounds = newBounds(
				-9.395869166756096, 4.373346528513022, -5.3507289140592444, 4.779874762138851, 1245, 916);
		GeoElement curve = evaluateGeoElement("x^4 + y^4 = 1000");

		ContourInfo info = builder.withImplicitCurve(curve).withBounds(bounds).build();
		clipper = new PerimeterContourClipper(info.getAssembler());
		clipper.setPolynomial(info.getPolynomial());
		clipper.clip(bounds);

		for (ClippedFragment fragment : clipper.getFragments()) {
			if (fragment.closed()) {
				continue;
			}
			assertNotNull(fragment.start());
			assertNotNull(fragment.end());
			assertFalse(samePoint(fragment.start().getPoint(), fragment.end().getPoint()));
		}
	}

	@Test
	void topRightCornerShouldBeInside() {
		bounds = newBounds(
				-9.395869166756096, 4.373346528513022, -5.3507289140592444, 4.779874762138851, 1245, 916);
		GeoElement curve = evaluateGeoElement("x^4 + y^4 = 1000");

		ContourInfo info = builder.withImplicitCurve(curve).withBounds(bounds).build();
		MyPoint topRightCorner = ClipEdge.TOP.cwCorner(info.getClipRect());
		Log.debug("topRight: " + topRightCorner);
		BernsteinPolynomial2D polynomial = info.getPolynomial();
		assertTrue(isInsideTest(polynomial, topRightCorner));
	}

	private boolean isInsideTest(BernsteinPolynomial2D polynomial, MyPoint topRightCorner) {
		return PerimeterContourClipper.isInsideTest(
				bounds, polynomial, topRightCorner.x, topRightCorner.y);
	}

	@Test
	void emptyBugTest() {
		bounds = boundsFromJSON("{\"invXscale\":0.02,\"invYscale\":0.020000000000000007,"
				+ "\"xMin\":-19.63,\"yMin\":2.529999999999992,"
				+ "\"width\":1091,\"height\":795}");
		GeoFunctionNVar f = evaluateGeoElement("x^3 < y^3");
		ExpressionNode expr = f.getFunctionExpression();
		Inequality inequality = new Inequality(
				getKernel(),
				expr.getLeft(),
				expr.getRight(),
				expr.getOperation(),
				f.getFunctionVariables());
		GeoImplicitCurve curve = inequality.getImplicitCurveBorder();

		ContourInfo info = builder.withImplicitCurve(curve).withBounds(bounds).build();
		BernsteinPolynomial2D polynomial = info.getPolynomial();
		ClipRect clipRect = info.getClipRect();
		assertTrue(isInsideTest(polynomial, ClipEdge.BOTTOM.cwCorner(clipRect)));
		assertTrue(isInsideTest(polynomial, ClipEdge.LEFT.cwCorner(clipRect)));
		assertTrue(isInsideTest(polynomial, ClipEdge.TOP.cwCorner(clipRect)));
	}

	private static boolean samePoint(MyPoint p0, MyPoint p1) {
		return Math.abs(p0.x - p1.x) <= 1e-10 && Math.abs(p0.y - p1.y) <= 1e-10;
	}
}
