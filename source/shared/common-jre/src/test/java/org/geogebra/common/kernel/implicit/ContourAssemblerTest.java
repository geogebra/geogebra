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

package org.geogebra.common.kernel.implicit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.kernel.SegmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ContourAssemblerTest extends BaseCurveContourTestSetup {
	private static final SegmentEndPoint
			a = new SegmentEndPoint("A", 0, 0);
	private static final SegmentEndPoint
			b = new SegmentEndPoint("B", 0, 1);
	private static final SegmentEndPoint
			c = new SegmentEndPoint("C", 1, 0);
	private static final SegmentEndPoint
			d = new SegmentEndPoint("D", 1, 1);
	private static final SegmentEndPoint
			e = new SegmentEndPoint("E", 5, 5);
	private int count;

	@BeforeEach
	void setUp() {
		createContour();
		link(a, b);
		link(c, d);
	}

	@Test
	void testInitialState() {
		segmentsShouldBe("{C, D}", "{A, B}");
	}

	private void segmentsShouldBe(String... lists) {
		int count = lists.length;
		assertEquals(count, contour.contourCount());
		for (int i = 0; i < count; i++) {
			assertEquals(lists[i], contour.contourAt(i).toString());
		}
	}

	@Test
	void testAddSegmentAC() {
		link(a, c);
		segmentsShouldBe("{D, C, A, B}");
		assertAll(isMoveTo(d), isLineTo(c), isLineTo(a), isLineTo(b));
	}

	private static Executable isLineTo(SegmentEndPoint point) {
		return () -> assertEquals(SegmentType.LINE_TO, point.getSegmentType());
	}

	private static Executable isMoveTo(SegmentEndPoint point) {
		return () -> assertEquals(SegmentType.MOVE_TO, point.getSegmentType());
	}

	@Test
	void testAddSegmentAD() {
		link(a, d);
		segmentsShouldBe("{C, D, A, B}");
		assertAll(isMoveTo(c), isLineTo(d), isLineTo(a), isLineTo(b));
	}

	@Test
	void testAddSegmentBC() {
		link(b, c);
		segmentsShouldBe("{A, B, C, D}");
		assertAll(isMoveTo(a), isLineTo(b), isLineTo(c), isLineTo(d));
	}

	@Test
	void testAddSegmentBD() {
		link(b, d);
		segmentsShouldBe("{C, D, B, A}");
		assertAll(isMoveTo(c), isLineTo(d), isLineTo(b), isLineTo(a));
	}

	@Test
	void testAddSegmentAE() {
		link(a, e);
		segmentsShouldBe("{C, D}", "{E, A, B}");
		assertAll(isMoveTo(c), isLineTo(d),
				isMoveTo(e), isLineTo(a), isLineTo(b));
	}

	@Test
	void testAddSegmentBE() {
		link(b, e);
		segmentsShouldBe("{C, D}", "{A, B, E}");
		assertAll(isMoveTo(c), isLineTo(d),
				isMoveTo(a), isLineTo(b), isLineTo(e));
	}

	@Test
	void testAddSegmentCE() {
		link(c, e);
		segmentsShouldBe("{E, C, D}", "{A, B}");
		assertAll(isMoveTo(e), isLineTo(c), isLineTo(d),
				isMoveTo(a), isLineTo(b));
	}

	@Test
	void testAddSegmentDE() {
		link(d, e);
		segmentsShouldBe("{C, D, E}", "{A, B}");
		assertAll(isMoveTo(c), isLineTo(d), isLineTo(e),
				isMoveTo(a), isLineTo(b));
	}

	@Test
	void testForEachOneContour() {
		link(b, d);
		forEachShouldRunTimes(1);
	}

	@Test
	void testForEachTwoContours() {
		link(d, e);
		forEachShouldRunTimes(2);
	}

	private void forEachShouldRunTimes(int expected) {
		count = 0;
		contour.forEachContour(c -> count++);
		assertEquals(expected, count);
	}
}
