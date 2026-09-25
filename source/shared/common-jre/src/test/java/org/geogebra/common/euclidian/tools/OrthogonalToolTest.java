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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ORTHOGONAL;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.algos.AlgoOrthoLinePointLine;
import org.geogebra.common.kernel.algos.AlgoOrthoLinePointVector;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrthogonalToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_ORTHOGONAL);
	}

	@Test
	void pointThenLineShouldCreatePerpendicularLine() {
		add("A = (2, -3)");
		add("g: y = -1");

		clickRW(2, -3);
		clickRW(1, -1);

		assertAll(
				() -> checkContent("A = (2, -3)", "g: y = -1", "f: x = 2"),
				() -> assertInstanceOf(AlgoOrthoLinePointLine.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void lineThenPointShouldCreatePerpendicularLine() {
		add("A = (2, -3)");
		add("g: y = -1");

		clickRW(1, -1);
		clickRW(2, -3);

		assertAll(
				() -> checkContent("A = (2, -3)", "g: y = -1", "f: x = 2"),
				() -> assertInstanceOf(AlgoOrthoLinePointLine.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void emptyClickThenLineShouldCreatePointAndPerpendicularLine() {
		add("g: y = -1");

		clickRW(2, -3);
		clickRW(1, -1);

		assertAll(
				() -> checkContent("g: y = -1", "A = (2, -3)", "f: x = 2"),
				() -> assertTrue(lookup("A").isIndependent()),
				() -> assertInstanceOf(AlgoOrthoLinePointLine.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void lineThenEmptyClickShouldCreatePointAndPerpendicularLine() {
		add("g: y = -1");

		clickRW(1, -1);
		clickRW(2, -3);

		assertAll(
				() -> checkContent("g: y = -1", "A = (2, -3)", "f: x = 2"),
				() -> assertTrue(lookup("A").isIndependent()),
				() -> assertInstanceOf(AlgoOrthoLinePointLine.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void pointAndVectorShouldCreatePerpendicularLine() {
		add("A = (1, -1)");
		add("B = (3, -1)");
		add("v = Vector(A, B)");
		add("C = (2, -3)");

		clickRW(2, -3);
		clickRW(2, -1);

		assertAll(
				() -> checkContent("A = (1, -1)", "B = (3, -1)", "v = (2, 0)", "C = (2, -3)", "f: x = 2"),
				() -> assertInstanceOf(AlgoOrthoLinePointVector.class, lookup("f").getParentAlgorithm()));
	}

	@Test
	void pointAndLinearFunctionShouldCreatePerpendicularLine() {
		add("f(x) = -x - 1");
		add("A = (3, -1)");

		clickRW(3, -1);
		clickRW(2, -3);

		assertAll(
				() -> checkContent("f(x) = -x - 1", "A = (3, -1)", "g: x - y = 4"),
				() -> assertInstanceOf(AlgoOrthoLinePointLine.class, lookup("g").getParentAlgorithm()));
	}

	@Test
	void secondLinearFunctionShouldBeIgnoredWhileFirstIsSelected() {
		GeoElement firstFunction = add("f(x) = -x - 1");
		add("g(x) = x - 6");
		add("A = (3, -1)");

		clickRW(2, -3);
		clickRW(4, -2);
		clickRW(3, -1);

		AlgoOrthoLinePointLine parentAlgorithm =
				assertInstanceOf(AlgoOrthoLinePointLine.class, lookup("h").getParentAlgorithm());
		assertAll(
				() -> checkContent("f(x) = -x - 1", "g(x) = x - 6", "A = (3, -1)", "h: x - y = 4"),
				() -> assertSame(firstFunction, parentAlgorithm.getInput()[1]));
	}

	@Test
	void lineThenLineShouldCreatePointOnSecondLine() {
		add("a: x = 1");
		GeoElement secondLine = add("b: y = -x - 1");

		clickRW(1, -4);
		clickRW(2, -3);

		GeoPoint point = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertAll(
				() -> checkContent("a: x = 1", "b: y = -x - 1", "A = (2, -3)", "f: y = -3"),
				() -> assertSame(secondLine, point.getPath()),
				() -> assertInstanceOf(AlgoOrthoLinePointLine.class, lookup("f").getParentAlgorithm()));
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
		AlgoOrthoLinePointLine parentAlgorithm =
				assertInstanceOf(AlgoOrthoLinePointLine.class, lookup("f").getParentAlgorithm());
		assertAll(
				() -> checkContent(
						"g: y = -1", "A = (1, -3)", "B = (1, -5)", "v = (0, -2)", "C = (1, -4)", "f: x = 1"),
				() -> assertSame(vector, point.getPath()),
				() -> assertSame(line, parentAlgorithm.getInput()[1]));
	}

	@Test
	void hoveringLineShouldHighlightWithoutCreatingPerpendicularLine() {
		GeoElement line = add("g: y = -1");

		moveMouseRW(2, -1);

		assertAll(
				() -> assertTrue(ec.getHighlightedgeos().contains(line)), () -> checkContent("g: y = -1"));
	}
}
