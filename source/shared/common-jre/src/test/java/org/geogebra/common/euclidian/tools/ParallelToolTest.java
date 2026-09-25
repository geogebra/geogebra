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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoLinePointLine;
import org.geogebra.common.kernel.algos.AlgoLinePointVector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParallelToolTest extends BaseToolTest {
	@BeforeEach
	void setMode() {
		setMode(EuclidianConstants.MODE_PARALLEL);
	}

	@Test
	void pointThenLineShouldCreateParallelLine() {
		add("A = (5, -2)");
		add("l1: y = -1");

		clickRW(5, -2);
		clickRW(2, -1);
		assertAll(
				() -> checkContent("A = (5, -2)", "l1: y = -1", "f: y = -2"),
				() -> assertInstanceOf(AlgoLinePointLine.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void lineThenPointShouldCreateParallelLine() {
		add("A = (5, -2)");
		add("l1: y = -1");

		clickRW(2, -1);
		clickRW(5, -2);
		assertAll(
				() -> checkContent("A = (5, -2)", "l1: y = -1", "f: y = -2"),
				() -> assertInstanceOf(AlgoLinePointLine.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void emptyClickThenLineShouldCreatePointAndParallelLine() {
		add("l1: y = -1");

		clickRW(5, -2);
		clickRW(2, -1);
		assertAll(
				() -> checkContent("l1: y = -1", "A = (5, -2)", "f: y = -2"),
				() -> assertTrue(lookup("A").isIndependent()),
				() -> assertInstanceOf(AlgoLinePointLine.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void lineThenEmptyClickShouldCreatePointAndParallelLine() {
		add("l1: y = -1");

		clickRW(2, -1);
		clickRW(5, -2);
		assertAll(
				() -> checkContent("l1: y = -1", "A = (5, -2)", "f: y = -2"),
				() -> assertTrue(lookup("A").isIndependent()),
				() -> assertInstanceOf(AlgoLinePointLine.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void pointThenVectorShouldCreateParallelLine() {
		add("A = (5, -2)");
		add("B = (-1, -1)");
		add("C = (5, -1)");
		add("v = Vector(B, C)");

		clickRW(5, -2);
		clickRW(3, -1);

		assertAll(
				() -> checkContent("A = (5, -2)", "B = (-1, -1)", "C = (5, -1)", "v = (6, 0)", "f: y = -2"),
				() -> assertInstanceOf(AlgoLinePointVector.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void vectorThenPointShouldCreateParallelLine() {
		add("A = (5, -2)");
		add("B = (-1, -1)");
		add("C = (5, -1)");
		add("v = Vector(B, C)");

		clickRW(3, -1);
		clickRW(5, -2);

		assertAll(
				() -> checkContent("A = (5, -2)", "B = (-1, -1)", "C = (5, -1)", "v = (6, 0)", "f: y = -2"),
				() -> assertInstanceOf(AlgoLinePointVector.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void pointThenLinearFunctionShouldCreateParallelLine() {
		add("A = (5, -2)");
		add("f(x) = 2x");

		clickRW(5, -2);
		clickRW(0, 0);
		assertAll(
				() -> checkContent("A = (5, -2)", "f(x) = 2x", "g: 2x - y = 12"),
				() -> assertInstanceOf(AlgoLinePointLine.class, lookup("g").getParentAlgorithm()));
	}

	@Test
	void secondLinearFunctionShouldBeIgnoredWhileFirstIsSelected() {
		GeoElement firstFunction = add("f(x) = -x - 1");
		add("g(x) = x - 6");
		add("A = (3, -1)");

		clickRW(2, -3);
		clickRW(4, -2);
		clickRW(3, -1);

		AlgoLinePointLine parentAlgorithm =
				assertInstanceOf(AlgoLinePointLine.class, lookup("h").getParentAlgorithm());
		assertAll(
				() -> checkContent("f(x) = -x - 1", "g(x) = x - 6", "A = (3, -1)", "h: -x - y = -2"),
				() -> assertSame(firstFunction, parentAlgorithm.getInput()[1]));
	}

	@Test
	void linearFunctionThenPointShouldCreateParallelLine() {
		add("A = (5, -2)");
		add("f(x) = 2x");

		clickRW(0, 0);
		clickRW(5, -2);
		assertAll(
				() -> checkContent("A = (5, -2)", "f(x) = 2x", "g: 2x - y = 12"),
				() -> assertInstanceOf(AlgoLinePointLine.class, lookup("g").getParentAlgorithm()));
	}

	@Test
	void lineThenLineShouldCreatePointOnSecondLine() {
		GeoElement firstLine = add("a: x = 1");
		GeoElement secondLine = add("b: y = -x - 1");

		clickRW(1, -4);
		clickRW(2, -3);

		GeoPoint point = assertInstanceOf(GeoPoint.class, lookup("A"));
		AlgoLinePointLine parentAlgorithm =
				assertInstanceOf(AlgoLinePointLine.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent("a: x = 1", "b: y = -x - 1", "A = (2, -3)", "f: x = 2"),
				() -> assertSame(secondLine, point.getPath()),
				() -> assertSame(firstLine, parentAlgorithm.getInput()[1]));
	}

	@Test
	void lineThenVectorShouldCreatePointOnVector() {
		GeoElement line = add("g: y = -1");
		add("A = (1, -3)");
		add("B = (1, -5)");
		GeoElement vector = add("v = Vector(A, B)");

		clickRW(3, -1);
		clickRW(1, -4);

		GeoPoint point = assertInstanceOf(GeoPoint.class, lookup("C"));
		AlgoLinePointLine parentAlgorithm =
				assertInstanceOf(AlgoLinePointLine.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(
						"g: y = -1", "A = (1, -3)", "B = (1, -5)", "v = (0, -2)", "C = (1, -4)", "f: y = -4"),
				() -> assertSame(vector, point.getPath()),
				() -> assertSame(line, parentAlgorithm.getInput()[1]));
	}

	@Test
	void lineThenPolygonInteriorShouldCreateFreePoint() {
		GeoElement line = add("g: y = -1");
		add("A = (1, -2)");
		add("B = (5, -2)");
		add("C = (5, -5)");
		add("D = (1, -5)");
		add("p = Polygon(A, B, C, D)");

		clickRW(6, -1);
		clickRW(3, -3);

		GeoPoint point = assertInstanceOf(GeoPoint.class, lookup("E"));
		AlgoLinePointLine parentAlgorithm =
				assertInstanceOf(AlgoLinePointLine.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> assertTrue(point.isIndependent()),
				() -> assertEquals("E = (3, -3)", point.toString(StringTemplate.defaultTemplate)),
				() -> assertEquals("f: y = -3", lookup("f").toString(StringTemplate.editTemplate)),
				() -> assertSame(line, parentAlgorithm.getInput()[1]));
	}

	@Test
	void hoveringLineShouldHighlightWithoutCreatingParallelLine() {
		GeoElement line = add("g: y = -1");

		moveMouseRW(2, -1);

		assertAll(
				() -> assertTrue(ec.getHighlightedgeos().contains(line)), () -> checkContent("g: y = -1"));
	}

	@Test
	void hoveringSecondLineShouldNotReplaceSelectedLine() {
		GeoElement firstLine = add("a: x = 1");
		GeoElement secondLine = add("b: y = -1");
		add("A = (3, -3)");

		clickRW(1, -3);
		moveMouseRW(3, -1);
		boolean secondLineHighlighted = ec.getHighlightedgeos().contains(secondLine);
		clickRW(3, -3);

		AlgoLinePointLine parentAlgorithm =
				assertInstanceOf(AlgoLinePointLine.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent("a: x = 1", "b: y = -1", "A = (3, -3)", "f: x = 3"),
				() -> assertFalse(secondLineHighlighted),
				() -> assertSame(firstLine, parentAlgorithm.getInput()[1]));
	}
}
