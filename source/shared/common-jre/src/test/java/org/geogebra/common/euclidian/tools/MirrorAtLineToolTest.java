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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIRROR_AT_LINE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.common.kernel.matrix.Coords;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/** Tests for reflecting objects across a line with the reflection tool. */
class MirrorAtLineToolTest extends BaseMirrorToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_MIRROR_AT_LINE);
	}

	@Test
	void lineThenPointShouldCreateReflectedPoint() {
		GeoPoint point = add("A = (1, -2)");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -1);
		checkContent("A = (1, -2)", "g: x = 3");
		clickRW(1, -2);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertAll(
				() -> checkContent("A = (1, -2)", "g: x = 3", "A' = (5, -2)"),
				() -> assertMirrorInputs(reflected, point, line));
	}

	@ParameterizedTest
	@CsvSource({
		"x = 3, 3, -1, 5, -2, false",
		"y = -3, 4, -3, 1, -4, false",
		"y = -3, 4, -3, 1, -4, true",
		"x + y = 0, 3, -3, 2, -1, false",
		"x + y = 0, 3, -3, 2, -1, true"
	})
	void pointShouldReflectAcrossDifferentLinesInEitherOrder(
			@NonNull String equation,
			double lineX,
			double lineY,
			double expectedX,
			double expectedY,
			boolean lineFirst) {
		GeoPoint point = add("A = (1, -2)");
		GeoLine line = add("g: " + equation);
		Set<String> existingObjects = getObjectNames();

		clickObjectAndMirror(1, -2, lineX, lineY, lineFirst);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, expectedX, expectedY);
		assertMirrorInputs(reflected, point, line);
	}

	@Test
	void obliqueMirrorShouldPerpendicularlyBisectOriginalAndReflectedPoints() {
		GeoPoint point = add("A = (1, -2)");
		GeoLine line = add("g: 2x + 3y = -5");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -3);
		clickRW(1, -2);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		double midpointX = (point.getInhomX() + reflected.getInhomX()) / 2;
		double midpointY = (point.getInhomY() + reflected.getInhomY()) / 2;
		double displacementX = reflected.getInhomX() - point.getInhomX();
		double displacementY = reflected.getInhomY() - point.getInhomY();
		double normalLength = Math.hypot(line.getX(), line.getY());

		assertAll(
				() -> assertTrue(reflected.isDefined()),
				() -> assertPoint(point, 1, -2),
				() -> assertLineThrough(line, 2, -3),
				() -> assertLineThrough(line, -1, -1),
				() -> assertLineThrough(line, midpointX, midpointY),
				() -> assertEquals(
						0,
						(displacementX * line.getY() - displacementY * line.getX()) / normalLength,
						1E-8,
						"Displacement should be perpendicular to the mirror line"),
				() -> assertMirrorInputs(reflected, point, line));
	}

	@Test
	void reflectedPointShouldFollowBothInputsAndRecoverFromUndefinedMirror() {
		GeoPoint point = add("A = (1, -2)");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();
		clickRW(3, -1);
		clickRW(1, -2);
		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertMirrorInputs(reflected, point, line);

		point.setCoords(2, -3, 1);
		point.updateCascade();
		assertPoint(reflected, 4, -3);

		line.setCoords(0, 1, 1);
		line.updateCascade();
		assertPoint(reflected, 2, 1);
		assertPoint(point, 2, -3);

		line.setUndefined();
		line.updateCascade();
		assertFalse(reflected.isDefined());

		line.setCoords(1, 0, -3);
		line.updateCascade();
		assertPoint(reflected, 4, -3);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void emptyClickShouldCreatePointAndReflectItInEitherOrder(boolean lineFirst) {
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();
		clickObjectAndMirror(1, -2, 3, -1, lineFirst);

		GeoPoint point = assertInstanceOf(GeoPoint.class, lookup("A"));
		GeoPoint reflected = assertInstanceOf(GeoPoint.class, lookup("A'"));
		assertOnlyNewObjects(existingObjects, point, reflected);
		assertAll(
				() -> checkContent("g: x = 3", "A = (1, -2)", "A' = (5, -2)"),
				() -> assertTrue(point.isIndependent()),
				() -> assertMirrorInputs(reflected, point, line));
	}

	@Test
	void pointOnMirrorShouldProduceDistinctCoincidentPoint() {
		GeoPoint point = add("A = (3, -2)");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -2);
		clickRW(3, -1);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, 3, -2);
		assertMirrorInputs(reflected, point, line);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void secondSelectedLineShouldBeMirror(boolean verticalFirst) {
		GeoLine vertical = add("g: x = 3");
		GeoLine diagonal = add("h: x + y = 0");
		Set<String> existingObjects = getObjectNames();

		clickObjectAndMirror(3, -1, 1, -1, !verticalFirst);

		GeoLine reflected = getOnlyNewObject(existingObjects, GeoLine.class);
		assertOnlyNewObjects(existingObjects, reflected);
		GeoLine source = verticalFirst ? vertical : diagonal;
		GeoLine mirror = verticalFirst ? diagonal : vertical;
		assertMirrorInputs(reflected, source, mirror);
		assertLineThrough(reflected, verticalFirst ? 0 : 5, verticalFirst ? -3 : -1);
		assertLineThrough(reflected, verticalFirst ? 2 : 4, verticalFirst ? -3 : -2);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void circleShouldReflectInEitherOrder(boolean lineFirst) {
		GeoConic circle = add("c: (x - 1)^2 + (y + 2)^2 = 1");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickObjectAndMirror(1, -1, 3, -1, lineFirst);

		GeoConic reflected = getOnlyNewObject(existingObjects, GeoConic.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertAll(
				() -> assertTrue(reflected.isDefined()),
				() -> assertEquals(5, reflected.getMidpoint().getX(), 1E-8),
				() -> assertEquals(-2, reflected.getMidpoint().getY(), 1E-8),
				() -> assertEquals(1, reflected.getCircleRadius(), 1E-8),
				() -> assertMirrorInputs(reflected, circle, line));
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void listShouldReflectEachElementInEitherOrder(boolean lineFirst) {
		GeoList source = add("L = {(1, -1), (2, -2)}");
		GeoPoint first = assertInstanceOf(GeoPoint.class, source.get(0));
		GeoPoint second = assertInstanceOf(GeoPoint.class, source.get(1));
		source.setEuclidianVisible(true);
		source.updateRepaint();
		GeoLine mirror = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickObjectAndMirror(1, -1, 3, -1, lineFirst);

		GeoList reflected = getOnlyNewObject(existingObjects, GeoList.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertMirrorInputs(reflected, source, mirror);
		assertEquals(2, reflected.size());
		assertPoint(assertInstanceOf(GeoPoint.class, reflected.get(0)), 5, -1);
		assertPoint(assertInstanceOf(GeoPoint.class, reflected.get(1)), 4, -2);
		assertNotSame(first, reflected.get(0));
		assertNotSame(second, reflected.get(1));
		assertPoint(first, 1, -1);
		assertPoint(second, 2, -2);
		first.setCoords(2, -1, 1);
		source.updateCascade();
		assertPoint(assertInstanceOf(GeoPoint.class, reflected.get(0)), 4, -1);
		assertPoint(assertInstanceOf(GeoPoint.class, reflected.get(1)), 4, -2);
	}

	@Test
	void segmentThenLineShouldReflectEndpointsAndPreserveLength() {
		add("A = (1, -1)");
		add("B = (1, -3)");
		add("s = Segment(A, B)");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -2);
		clickRW(3, -1);

		GeoSegment reflected = getOnlyNewObject(existingObjects, GeoSegment.class);
		assertOnlyNewObjects(
				existingObjects, reflected, reflected.getStartPoint(), reflected.getEndPoint());
		assertAll(
				() -> assertPoint(reflected.getStartPoint(), 5, -1),
				() -> assertPoint(reflected.getEndPoint(), 5, -3),
				() -> assertEquals(2, reflected.getLength(), 1E-8),
				() -> assertMirrorInputs(reflected.getStartPoint(), lookup("A"), line),
				() -> assertMirrorInputs(reflected.getEndPoint(), lookup("B"), line));
	}

	@Test
	void rayThenLineShouldPreserveReflectedDirectionAndStartPoint() {
		add("A = (1, -1)");
		add("B = (2, -2)");
		add("r = Ray(A, B)");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickRW(1.5, -1.5);
		clickRW(3, -1);

		GeoRay reflected = getOnlyNewObject(existingObjects, GeoRay.class);
		GeoPoint reflectedEnd = assertInstanceOf(GeoPoint.class, lookup("B'"));
		assertOnlyNewObjects(existingObjects, reflected, reflected.getStartPoint(), reflectedEnd);
		assertAll(
				() -> assertPoint(reflected.getStartPoint(), 5, -1),
				() -> assertPoint(reflectedEnd, 4, -2),
				() -> assertMirrorInputs(reflected.getStartPoint(), lookup("A"), line),
				() -> assertMirrorInputs(reflectedEnd, lookup("B"), line),
				() -> assertSame(reflectedEnd, reflected.getParentAlgorithm().getInput()[1]),
				() -> assertTrue(reflected.isOnPath(new Coords(4, -2, 0, 1), 1E-8)),
				() -> assertTrue(reflected.isOnPath(new Coords(3, -3, 0, 1), 1E-8)),
				() -> assertFalse(reflected.isOnPath(new Coords(6, 0, 0, 1), 1E-8)));
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void vectorShouldReflectDirectionInEitherOrder(boolean lineFirst) {
		GeoVector vector = add("u = Vector((1, -1), (2, -3))");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickObjectAndMirror(1.5, -2, 3, -1, lineFirst);

		GeoVector reflected = getOnlyNewObject(existingObjects, GeoVector.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertAll(
				() -> assertTrue(reflected.isDefined()),
				() -> assertEquals(-1, reflected.getX(), 1E-8),
				() -> assertEquals(-2, reflected.getY(), 1E-8),
				() -> assertMirrorInputs(reflected, vector, line));
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void functionShouldBecomeReflectedCurveInEitherOrder(boolean lineFirst) {
		GeoElement function = add("f(x) = x^2 - 4");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickObjectAndMirror(1, -3, 3, -1, lineFirst);

		GeoCurveCartesian reflected = getOnlyNewObject(existingObjects, GeoCurveCartesian.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertTrue(reflected.isDefined());
		assertMirrorInputs(reflected, function, line);
		for (double parameter : new double[] {-1, 0, 2}) {
			double[] coordinates = new double[2];
			reflected.evaluateCurve(parameter, coordinates);
			assertEquals(6 - parameter, coordinates[0], 1E-8);
			assertEquals(parameter * parameter - 4, coordinates[1], 1E-8);
		}
	}

	@Test
	void segmentAsMirrorShouldUseItsSupportingLineBeyondEndpoints() {
		GeoPoint point = add("A = (1, -3)");
		GeoLine segment = add("s = Segment((3, -1), (3, -2))");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -1.5);
		clickRW(1, -3);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, 5, -3);
		assertMirrorInputs(reflected, point, segment);
	}

	@ParameterizedTest
	@ValueSource(booleans = {false, true})
	void polygonInteriorShouldReflectVerticesInEitherOrder(boolean lineFirst) {
		add("A = (1, -1)");
		add("B = (2, -1)");
		add("C = (1, -3)");
		GeoPolygon polygon = add("p = Polygon(A, B, C)");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickObjectAndMirror(1.25, -1.5, 3, -1, lineFirst);

		GeoPolygon reflected = getOnlyNewObject(existingObjects, GeoPolygon.class);
		assertPolygonStructure(reflected, polygon, line);
		assertOnlyNewPolygon(existingObjects, reflected);
		assertAll(
				() -> assertEquals(1, reflected.getArea(), 1E-8),
				() -> assertPoint(reflected.getPoint(0), 5, -1),
				() -> assertPoint(reflected.getPoint(1), 4, -1),
				() -> assertPoint(reflected.getPoint(2), 5, -3));
		polygon.getPoint(1).setCoords(3, -1, 1);
		polygon.getPoint(1).updateCascade();
		assertPoint(reflected.getPoint(0), 5, -1);
		assertPoint(reflected.getPoint(1), 3, -1);
		assertPoint(reflected.getPoint(2), 5, -3);
		assertEquals(2, polygon.getArea(), 1E-8);
		assertEquals(2, reflected.getArea(), 1E-8);
		assertEquals(2, reflected.getSegments()[0].getLength(), 1E-8);
	}

	@Test
	void rectangleSelectionShouldReflectAllSelectedPoints() {
		add("A = (1, -1)");
		add("B = (2, -2)");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		dragRW(0.5, -0.5, 2.5, -2.5);
		clickRW(3, -3);

		GeoPoint firstReflected = assertInstanceOf(GeoPoint.class, lookup("A'"));
		GeoPoint secondReflected = assertInstanceOf(GeoPoint.class, lookup("B'"));
		assertOnlyNewObjects(existingObjects, firstReflected, secondReflected);
		assertPoint(firstReflected, 5, -1);
		assertPoint(secondReflected, 4, -2);
		assertMirrorInputs(firstReflected, lookup("A"), line);
		assertMirrorInputs(secondReflected, lookup("B"), line);
	}

	@Test
	void selectingPolygonAfterPointShouldReflectOnlyPolygon() {
		add("P = (4, -4)");
		add("A = (1, -1)");
		add("B = (2, -1)");
		add("C = (1, -3)");
		GeoPolygon polygon = add("p = Polygon(A, B, C)");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickRW(4, -4);
		clickRW(1.25, -1.5);
		assertEquals(existingObjects, getObjectNames());
		clickRW(3, -1);

		GeoPolygon reflected = getOnlyNewObject(existingObjects, GeoPolygon.class);
		assertPolygonStructure(reflected, polygon, line);
		assertOnlyNewPolygon(existingObjects, reflected);
		assertAll(
				() -> assertPoint(reflected.getPoint(0), 5, -1),
				() -> assertPoint(reflected.getPoint(1), 4, -1),
				() -> assertPoint(reflected.getPoint(2), 5, -3));
	}

	@Test
	void rectangleAroundPolygonShouldNotDuplicateReflectedVertices() {
		add("A = (1, -1)");
		add("B = (2, -1)");
		add("C = (1, -2)");
		GeoPolygon polygon = add("p = Polygon(A, B, C)");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		dragRW(0.5, -0.5, 2.5, -2.5);
		clickRW(3, -3);

		GeoPolygon reflected = getOnlyNewObject(existingObjects, GeoPolygon.class);
		assertPolygonStructure(reflected, polygon, line);
		assertOnlyNewPolygon(existingObjects, reflected);
		assertAll(
				() -> assertPoint(reflected.getPoint(0), 5, -1),
				() -> assertPoint(reflected.getPoint(1), 4, -1),
				() -> assertPoint(reflected.getPoint(2), 5, -2));
	}

	@Test
	void hoveringShouldHighlightWithoutCreatingObjectsOrReplacingSelectedMirror() {
		GeoPoint point = add("A = (1, -2)");
		GeoLine line = add("g: x = 3");
		GeoLine otherLine = add("h: y = -3");
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(1, -2);
		assertTrue(ec.getHighlightedgeos().contains(point));
		moveMouseRW(3, -1);
		assertTrue(ec.getHighlightedgeos().contains(line));
		assertEquals(existingObjects, getObjectNames());

		clickRW(3, -1);
		moveMouseRW(4, -3);
		assertEquals(existingObjects, getObjectNames());
		assertTrue(ec.getHighlightedgeos().contains(otherLine));
		clickRW(1, -2);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, 5, -2);
		assertMirrorInputs(reflected, point, line);
	}

	@Test
	void secondPointShouldBeIgnoredWhileFirstPointIsSelected() {
		GeoPoint point = add("A = (1, -2)");
		add("B = (2, -3)");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -2);
		clickRW(2, -3);
		assertEquals(existingObjects, getObjectNames());
		clickRW(3, -1);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, 5, -2);
		assertMirrorInputs(reflected, point, line);
	}

	@Test
	void clickingSelectedPointAgainShouldDeselectIt() {
		add("A = (1, -2)");
		GeoPoint secondPoint = add("B = (2, -3)");
		GeoLine line = add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -2);
		resetMouseLocation();
		clickRW(1, -2);
		clickRW(3, -1);
		assertEquals(existingObjects, getObjectNames());
		clickRW(2, -3);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, 4, -3);
		assertMirrorInputs(reflected, secondPoint, line);
	}

	@Test
	void hoveringEmptySpaceShouldNotCreatePoint() {
		add("g: x = 3");
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(1, -2);
		clickRW(3, -1);
		moveMouseRW(1, -2);

		assertEquals(existingObjects, getObjectNames());
	}

	@Test
	void toolShouldClearSelectionAfterCompletion() {
		add("A = (1, -2)");
		add("B = (2, -3)");
		add("g: x = 3");
		GeoLine secondMirror = add("h: y = -4");

		clickRW(3, -1);
		clickRW(1, -2);
		Set<String> existingObjects = getObjectNames();
		clickRW(2, -3);
		assertEquals(existingObjects, getObjectNames());
		clickRW(4, -4);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, 2, -5);
		assertMirrorInputs(reflected, lookup("B"), secondMirror);
	}

	private void clickObjectAndMirror(
			double objectX, double objectY, double lineX, double lineY, boolean lineFirst) {
		if (lineFirst) {
			clickRW(lineX, lineY);
			clickRW(objectX, objectY);
		} else {
			clickRW(objectX, objectY);
			clickRW(lineX, lineY);
		}
	}
}
