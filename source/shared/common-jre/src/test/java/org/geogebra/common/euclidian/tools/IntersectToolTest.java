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

import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoIntersectConics;
import org.geogebra.common.kernel.algos.AlgoIntersectCurveCurve;
import org.geogebra.common.kernel.algos.AlgoIntersectFunctionLineNewton;
import org.geogebra.common.kernel.algos.AlgoIntersectLineCurve;
import org.geogebra.common.kernel.algos.AlgoIntersectLinePolyLine;
import org.geogebra.common.kernel.algos.AlgoIntersectNpFunctionPolyLine;
import org.geogebra.common.kernel.algos.AlgoIntersectPolyLines;
import org.geogebra.common.kernel.algos.AlgoIntersectPolynomialConic;
import org.geogebra.common.kernel.algos.AlgoIntersectPolynomialLine;
import org.geogebra.common.kernel.algos.AlgoIntersectPolynomialPolyLine;
import org.geogebra.common.kernel.algos.AlgoIntersectSingle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.implicit.AlgoIntersectImplicitPolynomials;
import org.geogebra.common.kernel.implicit.AlgoIntersectImplicitpolyParametric;
import org.geogebra.common.kernel.implicit.AlgoIntersectImplicitpolyPolyLine;
import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class IntersectToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(EuclidianConstants.MODE_INTERSECT);
	}

	@Test
	void intersectToolTwoLines() {
		add("Line((0, -1), (4, -1))");
		add("Line((2, 0), (2, -4))");
		click(50, 50);
		click(100, 100);
		checkContent("f: y = -1", "g: x = 2", "A = (2, -1)");
	}

	@Test
	void intersectToolTwoLinesAtIntersection() {
		add("Line((0, -1), (4, -1))");
		add("Line((2, 0), (2, -4))");
		click(100, 50);

		checkContent("f: y = -1", "g: x = 2", "A = (2, -1)");
	}

	@Test
	void intersectToolPreviewDoesNotCreatePoint() {
		GeoElement horizontal = add("f: y = -1");
		GeoElement vertical = add("g: x = 2");
		ec.wrapMouseMoved(new TestEvent(100, 50));

		assertTrue(ec.getHighlightedgeos().contains(horizontal));
		assertTrue(ec.getHighlightedgeos().contains(vertical));
		checkContent("f: y = -1", "g: x = 2");
	}

	@Test
	void intersectToolEmptyClickDoesNotCreatePoint() {
		click(100, 100);

		checkContent();
	}

	@Test
	void intersectToolLineCircleCreatesAllIntersections() {
		add("f: y = -1");
		add("c: Circle((2, -1), 2)");
		click(100, 50);
		click(100, 150);

		checkContent("f: y = -1", "c: (x - 2)² + (y + 1)² = 4", "A = (0, -1)", "B = (4, -1)");
	}

	@Test
	void intersectToolLineCircleAtIntersectionCreatesClosestPoint() {
		add("f: y = -1");
		add("c: Circle((2, -1), 2)");
		click(0, 50);

		checkContent("f: y = -1", "c: (x - 2)² + (y + 1)² = 4", "A = (0, -1)");
	}

	@Test
	void intersectToolPolynomialFunctionsCreatesAllIntersections() {
		add("f(x) = x^2 - 1");
		add("g(x) = 0");
		click(0, 50);
		click(0, 0);

		checkContent("f(x) = x² - 1", "g(x) = 0", "A = (-1, 0)", "B = (1, 0)");
	}

	@Test
	void intersectToolPolynomialFunctionsAtIntersectionCreatesClosestPoint() {
		add("f(x) = x^2 - 1");
		add("g(x) = 0");
		click(50, 0);

		checkContent("f(x) = x² - 1", "g(x) = 0", "A = (1, 0)");
	}

	@Test
	void intersectToolChoosesLineAndConicWhenThreeObjectsAreHit() {
		add("f: y = 0");
		add("g: x = 0");
		add("c: Circle((1, 0), 1)");
		click(0, 0);

		checkContent("f: y = 0", "g: x = 0", "c: (x - 1)² + y² = 1", "A = (0, 0)");
		assertEquals("Intersect(c, f, 1)", lookup("A").getDefinition(StringTemplate.testTemplate));
	}

	@Test
	void intersectToolIgnoresAxesWhenMoreThanTwoObjectsAreHit() {
		getApp().getActiveEuclidianView().setShowAxes(true, true);
		add("f: x = 0");
		add("c: Circle((1, 0), 1)");
		click(0, 0);

		checkContent("f: x = 0", "c: (x - 1)² + y² = 1", "A = (0, 0)");
	}

	@Test
	void intersectToolThreeLines() {
		add("Line((0, -1), (4, -1))");
		add("Line((2, 0), (2, -4))");
		add("Line((0, 0), (4, -4))");
		click(50, 50);
		click(100, 50);
		click(100, 100);
		checkContent(
				"f: y = -1", "g: x = 2", "h: x + y = 0", "A = (1, -1)", "B = (2, -1)", "C = (2, -2)");
	}

	@Test
	void intersectToolDoubleHit() {
		setMode(EuclidianConstants.MODE_INTERSECT);
		add("a:x=1");
		add("b:Ray((1,-1),(-1,-1))");
		add("c:Circle((1,-1),3)");
		click(200, 50); // hit circle
		click(50, 50); // hit both ray and line
		checkContent(
				"a: x = 1",
				"b: y = -1",
				unicode("c: (x - 1)^2 + (y + 1)^2 = 9"),
				"A = (1, 2)",
				"B = (1, -4)");
		checkHiddenContent();
	}

	@Test
	void intersectToolIncident() {
		setMode(EuclidianConstants.MODE_INTERSECT);
		add("a:x=1");
		add("b:Segment((1,-2),(1,5))");
		add("c:Circle((1,-1),3)");
		click(50, 50); // hit both incident lines
		click(200, 50); // hit circle
		checkContent("a: x = 1", "b = 7", unicode("c: (x - 1)^2 + (y + 1)^2 = 9"), "A = (1, 2)");
		checkHiddenContent();
	}

	@Test
	void intersectToolAbs() {
		setMode(EuclidianConstants.MODE_INTERSECT);
		// TODO AlgebraTest.enableCAS(app, false);
		add("f:abs(x-2)-2");
		add("g:1-2x");
		click(100, 100);
		click(150, 250);
		checkContent("f(x) = abs(x - 2) - 2", "g(x) = 1 - 2x", "A = (1, -1)");
	}

	@ParameterizedTest(name = "{0}")
	@MethodSource("dispatchCases")
	void intersectToolDispatchesSupportedObjectPairs(IntersectionCase testCase) {
		GeoElement first = add(testCase.firstCommand);
		GeoElement second = add(testCase.secondCommand);
		Set<GeoElement> geosBeforeIntersection =
				new HashSet<>(getApp().getKernel().getConstruction().getGeoSetConstructionOrder());

		if (testCase.selectTogether) {
			click(testCase.firstX, testCase.firstY);
		} else {
			click(testCase.firstX, testCase.firstY);
			click(testCase.secondX, testCase.secondY);
		}

		List<GeoElement> newPoints =
				getApp().getKernel().getConstruction().getGeoSetConstructionOrder().stream()
						.filter(geo -> !geosBeforeIntersection.contains(geo))
						.filter(GeoElement::isGeoPoint)
						.toList();
		assertEquals(
				testCase.expectedOutputCount,
				newPoints.size(),
				testCase.name + " should create the expected number of outputs");
		assertPointValues(testCase, newPoints);
		for (GeoElement point : newPoints) {
			assertInstanceOf(testCase.expectedAlgorithm, point.getParentAlgorithm());
			List<GeoElement> inputs = List.of(point.getParentAlgorithm().getInput());
			assertTrue(inputs.contains(first), testCase.name + " should use the first object");
			assertTrue(inputs.contains(second), testCase.name + " should use the second object");
		}
	}

	private void assertPointValues(IntersectionCase testCase, List<GeoElement> newPoints) {
		List<ExpectedPoint> unmatched = new ArrayList<>(testCase.expectedDefinedPoints);
		for (GeoElement point : newPoints) {
			if (!point.isDefined()) {
				continue;
			}
			GeoPoint point2D = (GeoPoint) point;
			double x = point2D.getInhomX();
			double y = point2D.getInhomY();
			int match = findMatchingPoint(unmatched, x, y);
			assertTrue(match >= 0, testCase.name + " created unexpected point (" + x + ", " + y + ")");
			unmatched.remove(match);
		}
		assertTrue(unmatched.isEmpty(), testCase.name + " did not create " + unmatched);
	}

	private int findMatchingPoint(List<ExpectedPoint> points, double x, double y) {
		for (int i = 0; i < points.size(); i++) {
			ExpectedPoint expected = points.get(i);
			if (Math.abs(expected.x - x) < 1E-5 && Math.abs(expected.y - y) < 1E-5) {
				return i;
			}
		}
		return -1;
	}

	private static Stream<IntersectionCase> dispatchCases() {
		return Stream.of(
				all(
						"line-polyline",
						"l: y = -2",
						"p = Polyline({(0,-1),(2,-3),(4,-1)})",
						250,
						100,
						100,
						150,
						AlgoIntersectLinePolyLine.class,
						point(1, -2),
						point(3, -2)),
				all(
						"line-curve",
						"l: y = 0",
						"c = Curve(t,t^2-1,t,-2,2)",
						150,
						0,
						0,
						50,
						AlgoIntersectLineCurve.class,
						point(-1, 0),
						point(1, 0)),
				closest(
						"conic-conic closest",
						"c = Circle((2,-2),2)",
						"d = Circle((4,-2),2)",
						150,
						13,
						AlgoIntersectSingle.class,
						point(3, -2 + Math.sqrt(3))),
				all(
						"conic-conic all",
						"c = Circle((2,-2),2)",
						"d = Circle((4,-2),2)",
						100,
						0,
						300,
						100,
						AlgoIntersectConics.class,
						point(3, -2 + Math.sqrt(3)),
						point(3, -2 - Math.sqrt(3))),
				closest(
						"curve-curve closest",
						"c = Curve(t,-t,t,0,4)",
						"d = Curve(t,t-2,t,0,2)",
						50,
						50,
						AlgoIntersectCurveCurve.class,
						point(1, -1)),
				allUndefined(
						"curve-curve all",
						"c = Curve(t,-t,t,0,4)",
						"d = Curve(t,t-2,t,0,2)",
						150,
						150,
						0,
						100,
						AlgoIntersectCurveCurve.class,
						1),
				all(
						"line-polygon",
						"l: y = -2",
						"p = Polygon({(0,-1),(4,-1),(4,-3),(0,-3)})",
						250,
						100,
						100,
						75,
						AlgoIntersectLinePolyLine.class,
						point(0, -2),
						point(4, -2)),
				all(
						"polyline-polyline",
						"p = Polyline({(0,-1),(4,-3)})",
						"q = Polyline({(0,-3),(4,-1)})",
						50,
						75,
						50,
						125,
						AlgoIntersectPolyLines.class,
						point(2, -2)),
				all(
						"polygon-polygon",
						"p = Polygon({(0,-1),(4,-1),(4,-3),(0,-3)})",
						"q = Polygon({(1,0),(3,0),(3,-4),(1,-4)})",
						25,
						100,
						100,
						25,
						AlgoIntersectPolyLines.class,
						point(1, -1),
						point(3, -1),
						point(1, -3),
						point(3, -3)),
				closest(
						"line-function closest",
						"l: y = 0",
						"f(x) = x^2 - 1",
						50,
						0,
						AlgoIntersectSingle.class,
						point(1, 0)),
				closest(
						"line-function non-polynomial closest",
						"l: y = 0",
						"f(x) = sin(x)",
						0,
						0,
						AlgoIntersectFunctionLineNewton.class,
						point(0, 0)),
				all(
						"line-function all",
						"l: y = 0",
						"f(x) = x^2 - 1",
						100,
						0,
						0,
						50,
						AlgoIntersectPolynomialLine.class,
						point(-1, 0),
						point(1, 0)),
				all(
						"polynomial-polyline",
						"f(x) = (x-2)^2 - 3",
						"p = Polyline({(0,-2),(4,-2)})",
						100,
						150,
						100,
						100,
						AlgoIntersectPolynomialPolyLine.class,
						point(1, -2),
						point(3, -2)),
				all(
						"non-polynomial-polyline",
						"f(x) = -sin(x) - 1",
						"p = Polyline({(0,-1),(4,-1)})",
						50,
						92,
						100,
						50,
						AlgoIntersectNpFunctionPolyLine.class,
						point(Math.PI, -1)),
				all(
						"polynomial-polygon",
						"f(x) = -2",
						"p = Polygon({(0,-1),(4,-1),(4,-3),(0,-3)})",
						250,
						100,
						100,
						75,
						AlgoIntersectPolynomialPolyLine.class,
						point(0, -2),
						point(4, -2)),
				all(
						"non-polynomial-polygon",
						"f(x) = -sin(x) - 2",
						"p = Polygon({(0,-1),(4,-1),(4,-3),(0,-3)})",
						250,
						52,
						100,
						75,
						AlgoIntersectNpFunctionPolyLine.class,
						point(Math.PI / 2, -3)),
				closest(
						"function-conic closest",
						"f(x) = 0",
						"c: x^2 + y^2 = 1",
						50,
						0,
						AlgoIntersectSingle.class,
						point(1, 0)),
				all(
						"function-conic all",
						"f(x) = -2",
						"c: (x-2)^2 + (y+2)^2 = 1",
						200,
						100,
						100,
						50,
						AlgoIntersectPolynomialConic.class,
						point(1, -2),
						point(3, -2)),
				closest(
						"implicit-function closest",
						"i: x^3 + y^3 = 0",
						"f(x) = 0",
						0,
						0,
						AlgoIntersectSingle.class,
						point(0, 0)),
				all(
						"implicit-function all",
						"i: x^3 + y^3 = 0",
						"f(x) = 0",
						50,
						50,
						100,
						0,
						AlgoIntersectImplicitpolyParametric.class,
						point(0, 0)),
				closest(
						"implicit-line closest",
						"i: x^3 + y^3 = 0",
						"l: y = 0",
						0,
						0,
						AlgoIntersectSingle.class,
						point(0, 0)),
				all(
						"implicit-line all",
						"i: x^3 + y^3 = 0",
						"l: y = 0",
						50,
						50,
						100,
						0,
						AlgoIntersectImplicitpolyParametric.class,
						point(0, 0)),
				closest(
						"implicit-conic closest",
						"i: x^3 + y^3 = 0",
						"c: x^2 + y^2 = 1",
						35,
						35,
						AlgoIntersectSingle.class,
						point(Math.sqrt(0.5), -Math.sqrt(0.5))),
				all(
						"implicit-conic all",
						"i: (x-2)^3 + (y+2)^3 = 0",
						"c: (x-2)^2 + (y+2)^2 = 1",
						200,
						200,
						100,
						50,
						AlgoIntersectImplicitPolynomials.class,
						point(2 - Math.sqrt(0.5), -2 + Math.sqrt(0.5)),
						point(2 + Math.sqrt(0.5), -2 - Math.sqrt(0.5))),
				closest(
						"implicit-implicit closest",
						"i: x^3 + (y+2)^3 = 0",
						"j: (x-2)^3 - (y+2)^3 = 0",
						50,
						150,
						AlgoIntersectSingle.class,
						point(1, -3)),
				all(
						"implicit-implicit all",
						"i: x^3 + (y+2)^3 = 0",
						"j: (x-2)^3 - (y+2)^3 = 0",
						150,
						250,
						150,
						50,
						AlgoIntersectImplicitPolynomials.class,
						point(1, -3)),
				all(
						"implicit-polyline",
						"i: x^3 + (y+2)^3 = 0",
						"p = Polyline({(0,-4),(4,0)})",
						150,
						250,
						150,
						50,
						AlgoIntersectImplicitpolyPolyLine.class,
						point(1, -3)),
				all(
						"implicit-polygon",
						"i: x^3 + y^3 = 0",
						"p = Polygon({(1,-0.5),(3,-0.5),(3,-3.5),(1,-3.5)})",
						200,
						200,
						100,
						75,
						AlgoIntersectImplicitpolyPolyLine.class,
						point(1, -1),
						point(3, -3)));
	}

	private static IntersectionCase closest(
			String name,
			String firstCommand,
			String secondCommand,
			int x,
			int y,
			Class<?> algorithm,
			ExpectedPoint expectedPoint) {
		return new IntersectionCase(
				name, firstCommand, secondCommand, true, x, y, 0, 0, algorithm, 1, List.of(expectedPoint));
	}

	private static IntersectionCase all(
			String name,
			String firstCommand,
			String secondCommand,
			int firstX,
			int firstY,
			int secondX,
			int secondY,
			Class<?> algorithm,
			ExpectedPoint... expectedPoints) {
		return new IntersectionCase(
				name,
				firstCommand,
				secondCommand,
				false,
				firstX,
				firstY,
				secondX,
				secondY,
				algorithm,
				expectedPoints.length,
				List.of(expectedPoints));
	}

	private static IntersectionCase allUndefined(
			String name,
			String firstCommand,
			String secondCommand,
			int firstX,
			int firstY,
			int secondX,
			int secondY,
			Class<?> algorithm,
			int expectedOutputCount) {
		return new IntersectionCase(
				name,
				firstCommand,
				secondCommand,
				false,
				firstX,
				firstY,
				secondX,
				secondY,
				algorithm,
				expectedOutputCount,
				List.of());
	}

	private static ExpectedPoint point(double x, double y) {
		return new ExpectedPoint(x, y);
	}

	private record IntersectionCase(
			String name,
			String firstCommand,
			String secondCommand,
			boolean selectTogether,
			int firstX,
			int firstY,
			int secondX,
			int secondY,
			Class<?> expectedAlgorithm,
			int expectedOutputCount,
			List<ExpectedPoint> expectedDefinedPoints) {
		@Override
		public String toString() {
			return name;
		}
	}

	private record ExpectedPoint(double x, double y) {}
}
