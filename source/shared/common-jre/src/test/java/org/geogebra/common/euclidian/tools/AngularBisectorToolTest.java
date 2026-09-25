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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ANGULAR_BISECTOR;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.geogebra.common.kernel.algos.AlgoAngularBisectorLines;
import org.geogebra.common.kernel.algos.AlgoAngularBisectorPoints;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AngularBisectorToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_ANGULAR_BISECTOR);
	}

	@Test
	void threePointsShouldCreateAngleBisector() {
		GeoPoint pointA = add("A = (0, 0)");
		GeoPoint vertex = add("B = (0, -2)");
		GeoPoint pointC = add("C = (2, -2)");

		clickRW(0, 0);
		clickRW(0, -2);
		clickRW(2, -2);

		AlgoAngularBisectorPoints parentAlgorithm =
				assertInstanceOf(AlgoAngularBisectorPoints.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(
						"A = (0, 0)", "B = (0, -2)", "C = (2, -2)", "f: -0.70711x + 0.70711y = -1.41421"),
				() -> assertSame(pointA, parentAlgorithm.getA()),
				() -> assertSame(vertex, parentAlgorithm.getB()),
				() -> assertSame(pointC, parentAlgorithm.getC()));
	}

	@Test
	void twoLinesShouldCreateBothAngleBisectors() {
		GeoLine firstLine = add("a: x = 1");
		GeoLine secondLine = add("b: y = -1");

		clickRW(1, -3);
		clickRW(3, -1);

		AlgoAngularBisectorLines parentAlgorithm =
				assertInstanceOf(AlgoAngularBisectorLines.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(
						"a: x = 1",
						"b: y = -1",
						"f: 0.70711x + 0.70711y = 0",
						"g: -0.70711x + 0.70711y = -1.41421"),
				() -> assertSame(parentAlgorithm, lookup("g").getParentAlgorithm()),
				() -> assertSame(firstLine, parentAlgorithm.getg()),
				() -> assertSame(secondLine, parentAlgorithm.geth()));
	}

	@Test
	void emptyClickShouldNotCreatePointOrClearPointSelection() {
		GeoPoint pointA = add("A = (0, 0)");
		GeoPoint vertex = add("B = (0, -2)");
		GeoPoint pointC = add("C = (2, -2)");

		clickRW(0, 0);
		clickRW(3, -4);
		clickRW(0, -2);
		clickRW(2, -2);

		AlgoAngularBisectorPoints parentAlgorithm =
				assertInstanceOf(AlgoAngularBisectorPoints.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(
						"A = (0, 0)", "B = (0, -2)", "C = (2, -2)", "f: -0.70711x + 0.70711y = -1.41421"),
				() -> assertSame(pointA, parentAlgorithm.getA()),
				() -> assertSame(vertex, parentAlgorithm.getB()),
				() -> assertSame(pointC, parentAlgorithm.getC()));
	}

	@Test
	void pointThenLineShouldIgnoreLineAndKeepPointSelection() {
		GeoPoint pointA = add("A = (0, 0)");
		GeoPoint vertex = add("B = (0, -2)");
		GeoPoint pointC = add("C = (2, -2)");
		add("g: y = -4");

		clickRW(0, 0);
		clickRW(3, -4);
		clickRW(0, -2);
		clickRW(2, -2);

		AlgoAngularBisectorPoints parentAlgorithm =
				assertInstanceOf(AlgoAngularBisectorPoints.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(
						"A = (0, 0)",
						"B = (0, -2)",
						"C = (2, -2)",
						"g: y = -4",
						"f: -0.70711x + 0.70711y = -1.41421"),
				() -> assertSame(pointA, parentAlgorithm.getA()),
				() -> assertSame(vertex, parentAlgorithm.getB()),
				() -> assertSame(pointC, parentAlgorithm.getC()));
	}

	@Test
	void lineThenPointShouldIgnorePointAndKeepLineSelection() {
		GeoLine firstLine = add("a: x = 1");
		GeoLine secondLine = add("b: y = -1");
		add("A = (3, -3)");

		clickRW(1, -3);
		clickRW(3, -3);
		clickRW(3, -1);

		AlgoAngularBisectorLines parentAlgorithm =
				assertInstanceOf(AlgoAngularBisectorLines.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(
						"a: x = 1",
						"b: y = -1",
						"A = (3, -3)",
						"f: 0.70711x + 0.70711y = 0",
						"g: -0.70711x + 0.70711y = -1.41421"),
				() -> assertSame(firstLine, parentAlgorithm.getg()),
				() -> assertSame(secondLine, parentAlgorithm.geth()));
	}
}
