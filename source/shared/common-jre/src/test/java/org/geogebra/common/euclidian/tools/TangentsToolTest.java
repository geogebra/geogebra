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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_TANGENTS;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.algos.AlgoCommonTangents;
import org.geogebra.common.kernel.algos.AlgoTangentLine;
import org.geogebra.common.kernel.algos.AlgoTangentPoint;
import org.geogebra.common.kernel.cas.AlgoTangentCurve;
import org.geogebra.common.kernel.cas.AlgoTangentFunctionPoint;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.AlgoTangentImplicitpoly;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TangentsToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_TANGENTS);
	}

	@Test
	void pointOnConicShouldCreateOneTangent() {
		GeoConic conic = add("c: x^2 + y^2 = 25");
		GeoPoint point = add("A = (3, -4)");

		clickRW(3, -4);
		clickRW(4, -3);

		AlgoTangentPoint parentAlgorithm =
				assertInstanceOf(AlgoTangentPoint.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(unicode("c: x^2 + y^2 = 25"), "A = (3, -4)", "f: 3x - 4y = 25"),
				() -> assertSame(point, parentAlgorithm.getInput()[0]),
				() -> assertSame(conic, parentAlgorithm.getInput()[1]));
	}

	@Test
	void externalPointAndConicShouldCreateTwoTangents() {
		GeoConic conic = add("c: x^2 + y^2 = 4");
		GeoPoint point = add("A = (3, 0)");

		clickRW(3, 0);
		clickRW(0, -2);

		AlgoTangentPoint parentAlgorithm =
				assertInstanceOf(AlgoTangentPoint.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContentLabels("c", "A", "f", "g"),
				() -> assertTrue(lookup("f").isDefined()),
				() -> assertTrue(lookup("g").isDefined()),
				() -> assertSame(parentAlgorithm, lookup("g").getParentAlgorithm()),
				() -> assertSame(point, parentAlgorithm.getInput()[0]),
				() -> assertSame(conic, parentAlgorithm.getInput()[1]));
	}

	@Test
	void lineAndConicShouldCreateParallelTangents() {
		GeoConic conic = add("c: x^2 + y^2 = 4");
		GeoLine line = add("a: y = -4");

		clickRW(3, -4);
		clickRW(0, -2);

		AlgoTangentLine parentAlgorithm =
				assertInstanceOf(AlgoTangentLine.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(unicode("c: x^2 + y^2 = 4"), "a: y = -4", "f: y = 2", "g: y = -2"),
				() -> assertSame(parentAlgorithm, lookup("g").getParentAlgorithm()),
				() -> assertSame(line, parentAlgorithm.getInput()[0]),
				() -> assertSame(conic, parentAlgorithm.getInput()[1]));
	}

	@Test
	void twoConicsShouldCreateCommonTangents() {
		GeoConic firstConic = add("c: (x - 2)^2 + (y + 3)^2 = 1");
		GeoConic secondConic = add("d: (x - 6)^2 + (y + 3)^2 = 1");

		clickRW(2, -2);
		clickRW(6, -2);

		AlgoCommonTangents parentAlgorithm =
				assertInstanceOf(AlgoCommonTangents.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContentLabels("c", "d", "f", "g", "h", "i"),
				() -> assertTrue(lookup("f").isDefined()),
				() -> assertTrue(lookup("g").isDefined()),
				() -> assertTrue(lookup("h").isDefined()),
				() -> assertTrue(lookup("i").isDefined()),
				() -> assertSame(parentAlgorithm, lookup("g").getParentAlgorithm()),
				() -> assertSame(parentAlgorithm, lookup("h").getParentAlgorithm()),
				() -> assertSame(parentAlgorithm, lookup("i").getParentAlgorithm()),
				() -> assertSame(firstConic, parentAlgorithm.getInput()[0]),
				() -> assertSame(secondConic, parentAlgorithm.getInput()[1]));
	}

	@Test
	void functionThenPointShouldCreateTangent() {
		GeoFunction function = add("f(x) = x^2 - 4");
		GeoPoint point = add("A = (1, -1)");

		clickRW(0, -4);
		clickRW(1, -1);

		AlgoTangentFunctionPoint parentAlgorithm =
				assertInstanceOf(AlgoTangentFunctionPoint.class, lookup("g").getParentAlgorithm());
		assertAll(
				() -> checkContent(unicode("f(x) = x^2 - 4"), "A = (1, -1)", "g: y = 2x - 5"),
				() -> assertSame(point, parentAlgorithm.getInput()[0]),
				() -> assertSame(function, parentAlgorithm.getInput()[1]));
	}

	@Test
	void parametricCurveThenPointShouldCreateTangent() {
		GeoCurveCartesian curve = add("c = Curve(t, t^2 - 4, t, -3, 3)");
		GeoPoint point = add("A = c(1)");

		clickRW(0, -4);
		clickRW(1, -3);

		AlgoTangentCurve parentAlgorithm =
				assertInstanceOf(AlgoTangentCurve.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(unicode("c:(t, t^2 - 4)"), "A = (1, -3)", "f: y = 2x - 5"),
				() -> assertSame(point, parentAlgorithm.getInput()[0]),
				() -> assertSame(curve, parentAlgorithm.getInput()[1]));
	}

	@Test
	void implicitCurveThenPointShouldCreateTangents() {
		GeoImplicit implicitCurve = add("p: x^3 + y^2 = 1");
		GeoPoint point = add("A = (0, -1)");

		clickRW(1, 0);
		clickRW(0, -1);

		AlgoTangentImplicitpoly parentAlgorithm =
				assertInstanceOf(AlgoTangentImplicitpoly.class, lookup("f").getParentAlgorithm());
		assertAll(
				() ->
						checkContent(unicode("p: x^3 + y^2 = 1"), "A = (0, -1)", "f: y = -1", "g: 2x + y = -1"),
				() -> assertSame(parentAlgorithm, lookup("g").getParentAlgorithm()),
				() -> assertSame(point, parentAlgorithm.getInput()[0]),
				() -> assertSame(implicitCurve, parentAlgorithm.getInput()[1]));
	}

	@Test
	void emptyClickShouldNotCreatePointOrClearConicSelection() {
		GeoConic conic = add("c: x^2 + y^2 = 25");
		GeoPoint point = add("A = (3, -4)");

		clickRW(4, -3);
		clickRW(5, -5);
		clickRW(3, -4);

		AlgoTangentPoint parentAlgorithm =
				assertInstanceOf(AlgoTangentPoint.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(unicode("c: x^2 + y^2 = 25"), "A = (3, -4)", "f: 3x - 4y = 25"),
				() -> assertSame(point, parentAlgorithm.getInput()[0]),
				() -> assertSame(conic, parentAlgorithm.getInput()[1]));
	}

	@Test
	void pointThenLineShouldIgnoreLineAndUsePointWithConic() {
		GeoConic conic = add("c: x^2 + y^2 = 25");
		GeoPoint point = add("A = (3, -4)");
		add("a: y = -6");

		clickRW(3, -4);
		clickRW(0, -6);
		clickRW(4, -3);

		AlgoTangentPoint parentAlgorithm =
				assertInstanceOf(AlgoTangentPoint.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(
						unicode("c: x^2 + y^2 = 25"), "A = (3, -4)", "a: y = -6", "f: 3x - 4y = 25"),
				() -> assertSame(point, parentAlgorithm.getInput()[0]),
				() -> assertSame(conic, parentAlgorithm.getInput()[1]));
	}

	@Test
	void lineThenPointShouldIgnorePointAndUseLineWithConic() {
		GeoConic conic = add("c: x^2 + y^2 = 4");
		GeoLine line = add("a: y = -4");
		add("A = (3, -3)");

		clickRW(3, -4);
		clickRW(3, -3);
		clickRW(0, -2);

		AlgoTangentLine parentAlgorithm =
				assertInstanceOf(AlgoTangentLine.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(
						unicode("c: x^2 + y^2 = 4"), "a: y = -4", "A = (3, -3)", "f: y = 2", "g: y = -2"),
				() -> assertSame(line, parentAlgorithm.getInput()[0]),
				() -> assertSame(conic, parentAlgorithm.getInput()[1]));
	}
}
