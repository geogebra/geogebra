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

package org.geogebra.common.euclidian.plot.implicit.classification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.List;

import org.geogebra.common.euclidian.plot.implicit.classification.topology.EpsilonPolicy;
import org.geogebra.common.kernel.MyPoint;
import org.junit.jupiter.api.Test;

class RegionClassifierSegmentIntersectionTest {

	@Test
	void shouldAddParamsForProperInteriorCrossing() throws Exception {
		ContourSegment horizontal = segment(0, 0, 2, 0, 0, 0);
		ContourSegment vertical = segment(1, -1, 1, 1, 1, 0);

		invokeDoIntersectSegments(horizontal, vertical);
		normalize(horizontal, vertical);

		assertEquals(List.of(0.0, 0.5, 1.0), horizontal.getIntersectParams());
		assertEquals(List.of(0.0, 0.5, 1.0), vertical.getIntersectParams());
	}

	@Test
	void shouldDedupEndpointTouchAfterNormalization() throws Exception {
		ContourSegment first = segment(0, 0, 2, 0, 0, 0);
		ContourSegment second = segment(2, 0, 2, 2, 1, 0);

		invokeDoIntersectSegments(first, second);
		normalize(first, second);

		assertEquals(List.of(0.0, 1.0), first.getIntersectParams());
		assertEquals(List.of(0.0, 1.0), second.getIntersectParams());
	}

	@Test
	void shouldIgnoreParallelSegments() throws Exception {
		ContourSegment first = segment(0, 0, 2, 0, 0, 0);
		ContourSegment second = segment(0, 1, 2, 1, 1, 0);

		invokeDoIntersectSegments(first, second);
		normalize(first, second);

		assertEquals(List.of(0.0, 1.0), first.getIntersectParams());
		assertEquals(List.of(0.0, 1.0), second.getIntersectParams());
	}

	@Test
	void shouldIgnoreLineIntersectionsOutsideSegmentRange() throws Exception {
		ContourSegment first = segment(0, 0, 1, 0, 0, 0);
		ContourSegment second = segment(2, -1, 2, 1, 1, 0);

		invokeDoIntersectSegments(first, second);
		normalize(first, second);

		assertEquals(List.of(0.0, 1.0), first.getIntersectParams());
		assertEquals(List.of(0.0, 1.0), second.getIntersectParams());
	}

	@Test
	void shouldAddOverlapEndpointsForCollinearSegments() throws Exception {
		ContourSegment first = segment(0, 0, 4, 0, 0, 0);
		ContourSegment second = segment(2, 0, 6, 0, 1, 0);

		invokeDoIntersectSegments(first, second);
		normalize(first, second);

		assertEquals(List.of(0.0, 0.5, 1.0), first.getIntersectParams());
		assertEquals(List.of(0.0, 0.5, 1.0), second.getIntersectParams());
	}

	@Test
	void shouldIgnoreDisjointCollinearSegments() throws Exception {
		ContourSegment first = segment(0, 0, 1, 0, 0, 0);
		ContourSegment second = segment(2, 0, 3, 0, 1, 0);

		invokeDoIntersectSegments(first, second);
		normalize(first, second);

		assertEquals(List.of(0.0, 1.0), first.getIntersectParams());
		assertEquals(List.of(0.0, 1.0), second.getIntersectParams());
	}

	@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
	private static void invokeDoIntersectSegments(ContourSegment first, ContourSegment second)
			throws Exception {
		ContourIntersectionFinder finder =
				new ContourIntersectionFinder(new ContourSegmentRegistry(List.of()),
						EpsilonPolicy.defaults());
		Method method = ContourIntersectionFinder.class.getDeclaredMethod("doIntersectSegments",
					ContourSegment.class, ContourSegment.class);
		method.setAccessible(true);
		method.invoke(finder, first, second);
	}

	private static void normalize(ContourSegment... segments) {
		for (ContourSegment segment : segments) {
			segment.normalizeSortDedup(1e-12);
		}
	}

	private static ContourSegment segment(double x1, double y1, double x2, double y2,
			int contourId, int segmentIndex) {
		return new ContourSegment(new MyPoint(x1, y1), new MyPoint(x2, y2), contourId,
				segmentIndex);
	}
}
