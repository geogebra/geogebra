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

package org.geogebra.common.euclidian.tools;

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_LINE_BISECTOR;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.algos.AlgoLineBisector;
import org.geogebra.common.kernel.algos.AlgoLineBisectorSegment;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LineBisectorToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_LINE_BISECTOR);
	}

	@Test
	void twoExistingPointsShouldCreatePerpendicularBisector() {
		GeoPoint pointA = add("A = (1, -1)");
		GeoPoint pointB = add("B = (3, -3)");

		clickRW(1, -1);
		clickRW(3, -3);

		AlgoLineBisector parentAlgorithm =
				assertInstanceOf(AlgoLineBisector.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent("A = (1, -1)", "B = (3, -3)", "f: -x + y = -4"),
				() -> assertSame(pointA, parentAlgorithm.getA()),
				() -> assertSame(pointB, parentAlgorithm.getB()));
	}

	@Test
	void clickingSegmentShouldCreateItsPerpendicularBisector() {
		add("A = (1, -1)");
		add("B = (3, -3)");
		GeoSegment segment = add("s = Segment(A, B)");

		clickRW(2, -2);

		AlgoLineBisectorSegment parentAlgorithm =
				assertInstanceOf(AlgoLineBisectorSegment.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent("A = (1, -1)", "B = (3, -3)", "s = 2.82843", "f: -x + y = -4"),
				() -> assertSame(segment, parentAlgorithm.getSegment()));
	}

	@Test
	void emptyClickShouldNotCreatePoint() {
		clickRW(2, -3);

		checkContent();
	}

	@Test
	void segmentShouldBeIgnoredWhilePointIsSelected() {
		add("A = (1, -1)");
		add("B = (3, -3)");
		add("C = (4, -1)");
		add("D = (6, -1)");
		add("s = Segment(C, D)");

		clickRW(1, -1);
		clickRW(5, -1);
		clickRW(3, -3);

		assertAll(
				() -> checkContent(
						"A = (1, -1)", "B = (3, -3)", "C = (4, -1)", "D = (6, -1)", "s = 2", "f: -x + y = -4"),
				() -> assertInstanceOf(AlgoLineBisector.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void clickingSegmentEndpointShouldSelectPoint() {
		GeoPoint pointA = add("A = (1, -1)");
		add("B = (5, -1)");
		add("s = Segment(A, B)");
		GeoPoint pointC = add("C = (1, -5)");

		clickRW(1, -1);
		clickRW(1, -5);

		AlgoLineBisector parentAlgorithm =
				assertInstanceOf(AlgoLineBisector.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent("A = (1, -1)", "B = (5, -1)", "s = 4", "C = (1, -5)", "f: y = -3"),
				() -> assertSame(pointA, parentAlgorithm.getA()),
				() -> assertSame(pointC, parentAlgorithm.getB()));
	}

	@Test
	void clickingPolygonSideShouldCreateItsPerpendicularBisector() {
		add("A = (1, -1)");
		add("B = (5, -1)");
		add("C = (5, -5)");
		add("D = (1, -5)");
		add("p = Polygon(A, B, C, D)");

		clickRW(3, -1);

		GeoSegment side = assertInstanceOf(GeoSegment.class, lookup("a"));
		AlgoLineBisectorSegment parentAlgorithm =
				assertInstanceOf(AlgoLineBisectorSegment.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(
						"A = (1, -1)",
						"B = (5, -1)",
						"C = (5, -5)",
						"D = (1, -5)",
						"p = 16",
						"a = 4",
						"b = 4",
						"c = 4",
						"d = 4",
						"f: x = 3"),
				() -> assertSame(side, parentAlgorithm.getSegment()));
	}

	@Test
	void hoveringSegmentShouldHighlightWithoutCreatingBisector() {
		add("A = (1, -1)");
		add("B = (3, -3)");
		GeoElement segment = add("s = Segment(A, B)");

		moveMouseRW(2, -2);

		assertAll(
				() -> assertTrue(ec.getHighlightedgeos().contains(segment)),
				() -> checkContent("A = (1, -1)", "B = (3, -3)", "s = 2.82843"));
	}

	@Test
	void unsupportedLineShouldBeIgnored() {
		add("g: y = -1");

		clickRW(2, -1);

		checkContent("g: y = -1");
	}
}
