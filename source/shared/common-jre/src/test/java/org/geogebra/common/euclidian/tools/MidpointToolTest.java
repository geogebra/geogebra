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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIDPOINT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.advanced.AlgoCentroidPolygon;
import org.geogebra.common.kernel.algos.AlgoCenterConic;
import org.geogebra.common.kernel.algos.AlgoMidpoint;
import org.geogebra.common.kernel.algos.AlgoMidpointSegment;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MidpointToolTest extends BaseToolTest {
	@BeforeEach
	void setMode() {
		setMode(MODE_MIDPOINT);
	}

	@Test
	void midpointOfTwoExistingPointsShouldBeCreated() {
		add("A = (1, -1)");
		add("B = (3, -3)");

		clickRW(1, -1);
		clickRW(3, -3);

		assertAll(
				() -> checkContent("A = (1, -1)", "B = (3, -3)", "C = (2, -2)"),
				() -> assertInstanceOf(AlgoMidpoint.class, lookup("C").getParentAlgorithm()));
	}

	@Test
	void twoEmptyClicksShouldCreatePointsAndTheirMidpoint() {
		clickRW(1, -1);
		clickRW(3, -3);

		assertAll(
				() -> checkContent("A = (1, -1)", "B = (3, -3)", "C = (2, -2)"),
				() -> assertInstanceOf(AlgoMidpoint.class, lookup("C").getParentAlgorithm()));
	}

	@Test
	void clickingSegmentShouldCreateItsMidpoint() {
		add("A = (1, -1)");
		add("B = (3, -3)");
		add("s = Segment(A, B)");

		clickRW(1.5, -1.5);

		assertAll(
				() -> checkContent("A = (1, -1)", "B = (3, -3)", "s = 2.82843", "C = (2, -2)"),
				() -> assertInstanceOf(AlgoMidpointSegment.class, lookup("C").getParentAlgorithm()));
	}

	@Test
	void clickingConicShouldCreateItsCenter() {
		add("A = (3, -3)");
		add("c = Circle(A, 2)");

		clickRW(5, -3);

		assertAll(
				() -> checkContent("A = (3, -3)", "c: (x - 3)² + (y + 3)² = 4", "B = (3, -3)"),
				() -> assertInstanceOf(AlgoCenterConic.class, lookup("B").getParentAlgorithm()));
	}

	@Test
	void clickingPolygonShouldCreateItsCentroid() {
		add("A = (1, -1)");
		add("B = (5, -1)");
		add("C = (5, -5)");
		add("D = (1, -5)");
		add("p = Polygon(A, B, C, D)");

		clickRW(3, -3);

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
						"E = (3, -3)"),
				() -> assertInstanceOf(AlgoCentroidPolygon.class, lookup("E").getParentAlgorithm()));
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

		checkContent("A = (1, -1)", "B = (3, -3)", "C = (4, -1)", "D = (6, -1)", "s = 2");

		clickRW(3, -3);

		assertAll(
				() -> checkContent(
						"A = (1, -1)", "B = (3, -3)", "C = (4, -1)", "D = (6, -1)", "s = 2", "E = (2, -2)"),
				() -> assertInstanceOf(AlgoMidpoint.class, lookup("E").getParentAlgorithm()));
	}

	@Test
	void clickingPolygonSideShouldCreateSideMidpoint() {
		add("A = (1, -1)");
		add("B = (5, -1)");
		add("C = (5, -5)");
		add("D = (1, -5)");
		add("p = Polygon(A, B, C, D)");

		clickRW(3, -1);

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
						"E = (3, -1)"),
				() -> assertInstanceOf(AlgoMidpointSegment.class, lookup("E").getParentAlgorithm()));
	}

	@Test
	void hoveringSegmentShouldHighlightWithoutCreatingMidpoint() {
		add("A = (1, -1)");
		add("B = (3, -3)");
		GeoElement segment = add("s = Segment(A, B)");

		moveMouseRW(2, -2);

		assertAll(
				() -> assertTrue(ec.getHighlightedgeos().contains(segment)),
				() -> checkContent("A = (1, -1)", "B = (3, -3)", "s = 2.82843"));
	}
}
