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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoExtremumMulti;
import org.geogebra.common.kernel.algos.AlgoExtremumPolynomial;
import org.geogebra.common.kernel.algos.AlgoExtremumPolynomialInterval;
import org.geogebra.common.kernel.algos.AlgoVertexConic;
import org.geogebra.common.kernel.arithmetic.PolyFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExtremumToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(EuclidianConstants.MODE_EXTREMUM);
	}

	@Test
	void extremumToolPolynomialFunction() {
		add("x*(x-2)");
		click(50, 50);

		checkContent("f(x) = x (x - 2)", "A = (1, -1)");
	}

	@Test
	void extremumToolPolynomialFunctionWithMultipleExtrema() {
		add("f(x) = x^3 - 3x");

		List<GeoElement> points = applyExtremumTool(0, 0);

		assertEquals(List.of("(-1, 2)", "(1, -2)"), definedPointValues(points));
		assertInstanceOf(AlgoExtremumPolynomial.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void extremumToolLinearFunctionCreatesNoDefinedPoints() {
		add("f(x) = -2x - 1");

		List<GeoElement> points = applyExtremumTool(0, 50);

		assertFalse(points.isEmpty());
		assertTrue(definedPointValues(points).isEmpty());
		assertInstanceOf(AlgoExtremumPolynomial.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void extremumToolConditionalPolynomialFunction() {
		add("f(x) = If(-2 < x < 2, x^3 - 3x)");

		List<GeoElement> points = applyExtremumTool(0, 0);

		assertEquals(List.of("(-1, 2)", "(1, -2)"), definedPointValues(points));
		assertInstanceOf(AlgoExtremumPolynomialInterval.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void extremumToolNonPolynomialFunctionUsesVisibleRange() {
		add("f(x) = sin(x)");

		List<GeoElement> points = applyExtremumTool(0, 0);

		assertFalse(definedPointValues(points).isEmpty());
		for (GeoElement point : points) {
			if (point.isDefined()) {
				double x = ((GeoPoint) point).getInhomX();
				assertTrue(x >= getApp().getActiveEuclidianView().getXmin());
				assertTrue(x <= getApp().getActiveEuclidianView().getXmax());
			}
		}
		assertInstanceOf(AlgoExtremumMulti.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void extremumToolHighDegreePolynomial() {
		GeoFunction function = add("f(x) = x^26");
		PolyFunction polynomial =
				function.getFunction().expandToPolyFunction(function.getFunctionExpression(), false, true);
		assertNotNull(polynomial);
		assertTrue(polynomial.isMaxDegreeReached());

		List<GeoElement> points = applyExtremumTool(0, 0);

		assertEquals(List.of("(0, 0)"), definedPointValues(points));
		assertInstanceOf(AlgoExtremumPolynomial.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void extremumToolCircleCreatesFourVertices() {
		add("c: x^2 + y^2 = 4");

		List<GeoElement> points = applyExtremumTool(100, 0);

		assertEquals(4, definedPointValues(points).size());
		assertInstanceOf(AlgoVertexConic.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void extremumToolHyperbolaCreatesTwoDefinedVertices() {
		add("c: x^2 - y^2 = 1");

		List<GeoElement> points = applyExtremumTool(50, 0);

		assertEquals(2, points.size());
		assertEquals(2, definedPointValues(points).size());
		assertInstanceOf(AlgoVertexConic.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void extremumToolParabolaCreatesOneDefinedVertex() {
		add("c = Parabola((0, -1), xAxis)");

		List<GeoElement> points = applyExtremumTool(0, 25);

		assertEquals(1, points.size());
		assertEquals(1, definedPointValues(points).size());
		assertInstanceOf(AlgoVertexConic.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void extremumToolFunctionPreviewDoesNotCreatePoints() {
		GeoElement function = add("f(x) = x (x - 2)");
		ec.wrapMouseMoved(new TestEvent(50, 50));

		assertTrue(ec.getHighlightedgeos().contains(function));
		checkContent("f(x) = x (x - 2)");
	}

	@Test
	void extremumToolConicPreviewDoesNotCreatePoints() {
		GeoElement circle = add("c: x^2 + y^2 = 4");
		ec.wrapMouseMoved(new TestEvent(100, 0));

		assertTrue(ec.getHighlightedgeos().contains(circle));
		checkContent("c: x² + y² = 4");
	}

	@Test
	void extremumToolPrefersFunctionWhenFunctionAndConicAreHit() {
		add("f(x) = x^2");
		add("c: x^2 + (y + 1)^2 = 1");

		List<GeoElement> points = applyExtremumTool(0, 0);

		assertEquals(List.of("(0, 0)"), definedPointValues(points));
		assertInstanceOf(AlgoExtremumPolynomial.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void extremumToolEmptyClickDoesNothing() {
		click(100, 100);

		checkContent();
	}

	@Test
	void extremumToolUnsupportedObjectDoesNothing() {
		add("l: y = 0");

		List<GeoElement> points = applyExtremumTool(0, 0);

		assertTrue(points.isEmpty());
	}

	private List<GeoElement> applyExtremumTool(int x, int y) {
		Set<GeoElement> geosBeforeExtremum =
				new HashSet<>(getApp().getKernel().getConstruction().getGeoSetConstructionOrder());
		click(x, y);

		List<GeoElement> points = new ArrayList<>();
		for (GeoElement geo : getApp().getKernel().getConstruction().getGeoSetConstructionOrder()) {
			if (!geosBeforeExtremum.contains(geo) && geo.isGeoPoint()) {
				points.add(geo);
			}
		}
		return points;
	}

	private List<String> definedPointValues(List<GeoElement> points) {
		List<String> values = new ArrayList<>();
		for (GeoElement point : points) {
			if (point.isDefined()) {
				values.add(point.toValueString(StringTemplate.testTemplate));
			}
		}
		return values;
	}
}
