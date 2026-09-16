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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoIntersectLineConic;
import org.geogebra.common.kernel.algos.AlgoRoots;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomial;
import org.geogebra.common.kernel.algos.AlgoRootsPolynomialInterval;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RootsToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(EuclidianConstants.MODE_ROOTS);
	}

	@Test
	void rootsToolPolynomialFunction() {
		add("x*(x-2)");
		click(50, 50);

		checkContent("f(x) = x (x - 2)", "A = (0, 0)", "B = (2, 0)");
	}

	@Test
	void rootsToolPolynomialWithoutRealRootsCreatesNoDefinedPoints() {
		add("f(x) = -x^2 - 1");

		List<GeoElement> points = applyRootsTool(0, 50);

		assertFalse(points.isEmpty());
		assertTrue(definedPointValues(points).isEmpty());
		assertInstanceOf(AlgoRootsPolynomial.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void rootsToolSimplifiesFunctionForRootFinding() {
		add("f(x) = -sqrt(x^2 - 1)");

		List<GeoElement> points = applyRootsTool(100, 87);

		assertEquals(List.of("(-1, 0)", "(1, 0)"), definedPointValues(points));
		assertInstanceOf(AlgoRootsPolynomial.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void rootsToolConditionalPolynomialFunction() {
		add("f(x) = If(-2 < x < 2, x^2 - 1)");

		List<GeoElement> points = applyRootsTool(0, 50);

		assertEquals(List.of("(-1, 0)", "(1, 0)"), definedPointValues(points));
		assertInstanceOf(AlgoRootsPolynomialInterval.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void rootsToolNonPolynomialFunctionUsesVisibleRange() {
		add("f(x) = -sin(x)");

		List<GeoElement> points = applyRootsTool(50, 42);

		assertFalse(definedPointValues(points).isEmpty());
		for (GeoElement point : points) {
			if (point.isDefined()) {
				double x = ((GeoPoint) point).getInhomX();
				assertTrue(x >= getApp().getActiveEuclidianView().getXmin());
				assertTrue(x <= getApp().getActiveEuclidianView().getXmax());
			}
		}
		assertInstanceOf(AlgoRoots.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void rootsToolCircleCreatesXAxisIntersections() {
		add("c: x^2 + y^2 = 4");

		List<GeoElement> points = applyRootsTool(0, 100);

		assertEquals(List.of("(-2, 0)", "(2, 0)"), definedPointValues(points));
		assertInstanceOf(AlgoIntersectLineConic.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void rootsToolTangentConicCreatesOneDefinedPoint() {
		add("c: x^2 + (y + 1)^2 = 1");

		List<GeoElement> points = applyRootsTool(0, 0);

		assertEquals(List.of("(0, 0)"), definedPointValues(points));
		assertInstanceOf(AlgoIntersectLineConic.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void rootsToolLineCreatesXAxisIntersection() {
		add("l: y = x - 1");

		List<GeoElement> points = applyRootsTool(0, 50);

		assertEquals(List.of("(1, 0)"), definedPointValues(points));
		assertInstanceOf(AlgoRootsPolynomial.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void rootsToolFunctionPreviewDoesNotCreatePoints() {
		GeoElement function = add("f(x) = x (x - 2)");
		ec.wrapMouseMoved(new TestEvent(50, 50));

		assertTrue(ec.getHighlightedgeos().contains(function));
		checkContent("f(x) = x (x - 2)");
	}

	@Test
	void rootsToolConicPreviewDoesNotCreatePoints() {
		GeoElement circle = add("c: x^2 + y^2 = 4");
		ec.wrapMouseMoved(new TestEvent(100, 0));

		assertTrue(ec.getHighlightedgeos().contains(circle));
		checkContent("c: x² + y² = 4");
	}

	@Test
	void rootsToolLinePreviewDoesNotCreatePoints() {
		GeoElement line = add("l: y = x - 1");
		int constructionSize =
				getApp().getKernel().getConstruction().getGeoSetConstructionOrder().size();
		ec.wrapMouseMoved(new TestEvent(50, 0));

		assertTrue(ec.getHighlightedgeos().contains(line));
		assertEquals(
				constructionSize,
				getApp().getKernel().getConstruction().getGeoSetConstructionOrder().size());
	}

	@Test
	void rootsToolPrefersFunctionOverConicAndLine() {
		add("f(x) = -x^2");
		add("c: x^2 + (y + 1)^2 = 1");
		add("l: y = 0");

		List<GeoElement> points = applyRootsTool(0, 0);

		assertEquals(List.of("(0, 0)"), definedPointValues(points));
		assertInstanceOf(AlgoRootsPolynomial.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void rootsToolPrefersConicOverLine() {
		add("c: x^2 + (y + 1)^2 = 1");
		add("l: y = 0");

		List<GeoElement> points = applyRootsTool(0, 0);

		assertEquals(List.of("(0, 0)"), definedPointValues(points));
		assertInstanceOf(AlgoIntersectLineConic.class, points.get(0).getParentAlgorithm());
	}

	@Test
	void rootsToolEmptyClickDoesNothing() {
		click(100, 100);

		checkContent();
	}

	@Test
	void rootsToolUnsupportedObjectDoesNothing() {
		add("A = (1, -1)");

		List<GeoElement> points = applyRootsTool(50, 50);

		assertTrue(points.isEmpty());
	}

	private List<GeoElement> applyRootsTool(int x, int y) {
		Set<GeoElement> geosBeforeRoots =
				new HashSet<>(getApp().getKernel().getConstruction().getGeoSetConstructionOrder());
		click(x, y);

		List<GeoElement> points = new ArrayList<>();
		for (GeoElement geo : getApp().getKernel().getConstruction().getGeoSetConstructionOrder()) {
			if (!geosBeforeRoots.contains(geo) && geo.isGeoPoint()) {
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
