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

import static org.junit.jupiter.api.Assertions.*;

import org.geogebra.common.euclidian.plot.interval.EuclidianViewBounds;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.implicit.EuclidianViewBoundsRWSCMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClipEdgeTest {

	private ClipRect rect;
	private ClipEpsilon eps;

	@BeforeEach
	void setup() {
		EuclidianViewBounds bounds = new EuclidianViewBoundsRWSCMock(-10, 10, -10, 10,
				1000, 1000);
		rect = new ClipRect(bounds, 0);
		eps = new ClipEpsilon(1e-9, 1e-9);
	}

	@Test
	void testNextCW() {
		assertEquals(ClipEdge.RIGHT, ClipEdge.TOP.nextCW());
		assertEquals(ClipEdge.BOTTOM, ClipEdge.RIGHT.nextCW());
		assertEquals(ClipEdge.LEFT, ClipEdge.BOTTOM.nextCW());
		assertEquals(ClipEdge.TOP, ClipEdge.LEFT.nextCW());
	}

	@Test
	void testCwCorner() {
		assertPointEquals(-10, 10, ClipEdge.LEFT.cwCorner(rect));    // left -> top-left
		assertPointEquals(10, 10, ClipEdge.TOP.cwCorner(rect));      // top  -> top-right
		assertPointEquals(10, -10, ClipEdge.RIGHT.cwCorner(rect));   // right-> bottom-right
		assertPointEquals(-10, -10, ClipEdge.BOTTOM.cwCorner(rect)); // bottom-> bottom-left
	}

	@Test
	void testEdgeParam() {
		assertEquals(0.0, ClipEdge.TOP.edgeParam(new MyPoint(-10, 10), rect), 1e-12);
		assertEquals(0.5, ClipEdge.TOP.edgeParam(new MyPoint(0, 10), rect), 1e-12);
		assertEquals(1.0, ClipEdge.TOP.edgeParam(new MyPoint(10, 10), rect), 1e-12);

		assertEquals(0.0, ClipEdge.BOTTOM.edgeParam(new MyPoint(-10, -10), rect), 1e-12);
		assertEquals(0.5, ClipEdge.BOTTOM.edgeParam(new MyPoint(0, -10), rect), 1e-12);
		assertEquals(1.0, ClipEdge.BOTTOM.edgeParam(new MyPoint(10, -10), rect), 1e-12);

		assertEquals(0.0, ClipEdge.RIGHT.edgeParam(new MyPoint(10, 10), rect), 1e-12);
		assertEquals(0.5, ClipEdge.RIGHT.edgeParam(new MyPoint(10, 0), rect), 1e-12);
		assertEquals(1.0, ClipEdge.RIGHT.edgeParam(new MyPoint(10, -10), rect), 1e-12);

		assertEquals(0.0, ClipEdge.LEFT.edgeParam(new MyPoint(-10, 10), rect), 1e-12);
		assertEquals(0.5, ClipEdge.LEFT.edgeParam(new MyPoint(-10, 0), rect), 1e-12);
		assertEquals(1.0, ClipEdge.LEFT.edgeParam(new MyPoint(-10, -10), rect), 1e-12);
	}

	@Test
	void intersectSegmentHitsTopAtCenter() {
		// Vertical segment crossing y = ymax at x = 0 -> TOP at its midpoint (t=0.5)
		MyPoint point1 = new MyPoint(0, 12);  // above
		MyPoint point2 = new MyPoint(0, -12); // below
		EdgeHit hit = ClipEdge.TOP.intersectSegment(rect, point1, point2, 7, eps);

		assertNotNull(hit, "Expected an intersection on TOP");
		assertEquals(ClipEdge.TOP, hit.edge());
		assertEquals(0.5, hit.tOnEdge(), 1e-12);
		assertPointEquals(0, 10, hit.point());

		// perimeter s should be sBase(=0 for TOP) + t = 0.5
		assertEquals(0.5, hit.sPerimeter(), 1e-12);
	}

	@Test
	void intersectSegmentReturnsNullWhenOutsideRange() {
		// Horizontal segment above TOP (no crossing within [xmin,xmax])
		MyPoint point1 = new MyPoint(20, 11);
		MyPoint poinr2 = new MyPoint(30, 11);
		assertNull(ClipEdge.TOP.intersectSegment(rect, point1, poinr2, 0, eps));
	}

	@Test
	void intersectSegmentSnapsNearCornerTopRight() {
		// Cross TOP within corner epsilon of (xmax, ymax): should snap to that corner
		double near = 1e-10; // < eps.corner() (1e-9 default min)
		double x = rect.getXmax() - near;

		MyPoint point1 = new MyPoint(x, 12);
		MyPoint point2 = new MyPoint(x, 0);

		EdgeHit hit = ClipEdge.TOP.intersectSegment(rect, point1, point2, 0, eps);
		assertNotNull(hit, "Expected intersection near the top-right corner");

		// Should snap to exact corner
		assertPointEquals(rect.getXmax(), rect.getYmax(), hit.point());
		assertEquals(ClipEdge.TOP, hit.edge());
		// On TOP, snapped to x = xmax → t = 1, so s = 0 + 1 = 1
		assertEquals(1.0, hit.tOnEdge(), 1e-9);
		assertEquals(1.0, hit.sPerimeter(), 1e-9);
	}

	@Test
	void intersectSegmentHitsRightAtQuarter() {
		// Diagonal crossing RIGHT at y = 5 → t = (ymax - y)/range = (10 - 5)/20 = 0.25
		MyPoint point1 = new MyPoint(8, 6);
		MyPoint point2 = new MyPoint(12, 4);
		EdgeHit hit = ClipEdge.RIGHT.intersectSegment(rect, point1, point2, 42, eps);

		assertNotNull(hit, "Expected an intersection on RIGHT");
		assertEquals(ClipEdge.RIGHT, hit.edge());
		assertEquals(0.25, hit.tOnEdge(), 1e-12);
		assertPointEquals(10, 5, hit.point());

		// Perimeter s: RIGHT has sBase = 1, forward, so s = 1 + t
		assertEquals(1.25, hit.sPerimeter(), 1e-12);
	}

	@Test
	void testDotInward() {
		// Into TOP is downward (0,-1)
		assertTrue(ClipEdge.TOP.dotInward(0, -1) > 0);
		assertTrue(ClipEdge.TOP.dotInward(0, +1) < 0);

		// Into RIGHT is leftward (-1,0)
		assertTrue(ClipEdge.RIGHT.dotInward(-1, 0) > 0);
		assertTrue(ClipEdge.RIGHT.dotInward(+1, 0) < 0);
	}

	@Test
	void testMiddlePoint() {
		assertPointEquals(0, 10, ClipEdge.TOP.middlePoint(rect));
		assertPointEquals(10, 0, ClipEdge.RIGHT.middlePoint(rect));
		assertPointEquals(0, -10, ClipEdge.BOTTOM.middlePoint(rect));
		assertPointEquals(-10, 0, ClipEdge.LEFT.middlePoint(rect));
	}

	private static void assertPointEquals(double x, double y, MyPoint p) {
		assertNotNull(p, "point is null");
		assertEquals(x, p.x, 1e-9, "x");
		assertEquals(y, p.y, 1e-9, "y");
	}
}
