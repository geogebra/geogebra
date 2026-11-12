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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_MIRROR_AT_POINT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoFunction;
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

/** Tests for reflecting objects through a point with the reflection tool. */
class MirrorAtPointToolTest extends BaseMirrorToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_MIRROR_AT_POINT);
	}

	@ParameterizedTest
	@CsvSource({"1, -2, 3, -3, 5, -4", "3, -3, 1, -2, -1, -1"})
	void secondPointShouldBeCenter(
			double sourceX,
			double sourceY,
			double centerX,
			double centerY,
			double expectedX,
			double expectedY) {
		GeoPoint point = add("A = (" + sourceX + "," + sourceY + ")");
		GeoPoint center = add("O = (" + centerX + "," + centerY + ")");
		Set<String> existingObjects = getObjectNames();

		clickRW(sourceX, sourceY);
		assertEquals(existingObjects, getObjectNames());
		clickRW(centerX, centerY);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, expectedX, expectedY);
		assertMirrorInputs(reflected, point, center);
	}

	@Test
	void centerShouldBeMidpointOfOriginalAndReflectedPoints() {
		GeoPoint point = add("A = (1, -2)");
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -2);
		clickRW(3, -3);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertAll(
				() -> assertTrue(reflected.isDefined()),
				() -> assertPoint(point, 1, -2),
				() -> assertPoint(center, 3, -3),
				() ->
						assertEquals(center.getInhomX(), (point.getInhomX() + reflected.getInhomX()) / 2, 1E-8),
				() ->
						assertEquals(center.getInhomY(), (point.getInhomY() + reflected.getInhomY()) / 2, 1E-8),
				() -> assertMirrorInputs(reflected, point, center));
	}

	@ParameterizedTest
	@CsvSource({"false, true", "true, false", "false, false"})
	void emptyClicksShouldCreateMissingSourceOrCenter(boolean sourceExists, boolean centerExists) {
		if (sourceExists) {
			add("A = (1, -2)");
		}
		if (centerExists) {
			add("O = (3, -3)");
		}

		clickRW(1, -2);
		clickRW(3, -3);

		GeoPoint source = assertInstanceOf(GeoPoint.class, lookup("A"));
		GeoPoint center = assertInstanceOf(GeoPoint.class, lookup(centerExists ? "O" : "B"));
		GeoPoint reflected = assertInstanceOf(GeoPoint.class, lookup("A'"));
		assertAll(
				() -> assertPoint(source, 1, -2),
				() -> assertPoint(center, 3, -3),
				() -> assertPoint(reflected, 5, -4),
				() -> assertTrue(source.isIndependent()),
				() -> assertTrue(center.isIndependent()),
				() -> assertMirrorInputs(reflected, source, center),
				() -> assertEquals(3, getObjectNames().size()));
	}

	@Test
	void reflectedPointShouldFollowBothInputsAndRecoverFromUndefinedInputs() {
		GeoPoint point = add("A = (1, -2)");
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -2);
		clickRW(3, -3);
		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertMirrorInputs(reflected, point, center);

		point.setCoords(2, -1, 1);
		point.updateCascade();
		assertPoint(reflected, 4, -5);
		center.setCoords(4, -2, 1);
		center.updateCascade();
		assertPoint(reflected, 6, -3);
		assertPoint(point, 2, -1);

		center.setUndefined();
		center.updateCascade();
		assertFalse(reflected.isDefined());
		center.setCoords(3, -3, 1);
		center.updateCascade();
		assertPoint(reflected, 4, -5);

		point.setUndefined();
		point.updateCascade();
		assertFalse(reflected.isDefined());
		point.setCoords(1, -2, 1);
		point.updateCascade();
		assertPoint(reflected, 5, -4);
	}

	@Test
	void lineThenPointShouldCreateParallelReflectedLine() {
		GeoLine line = add("g: x = 1");
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -1);
		clickRW(3, -3);

		GeoLine reflected = getOnlyNewObject(existingObjects, GeoLine.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertLineThrough(reflected, 5, -1);
		assertLineThrough(reflected, 5, -4);
		assertMirrorInputs(reflected, line, center);
	}

	@Test
	void circleThenPointShouldReflectCenterAndPreserveRadius() {
		GeoConic circle = add("c: (x - 1)^2 + (y + 2)^2 = 1");
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -1);
		clickRW(3, -3);

		GeoConic reflected = getOnlyNewObject(existingObjects, GeoConic.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertAll(
				() -> assertTrue(reflected.isDefined()),
				() -> assertEquals(5, reflected.getMidpoint().getX(), 1E-8),
				() -> assertEquals(-4, reflected.getMidpoint().getY(), 1E-8),
				() -> assertEquals(1, reflected.getCircleRadius(), 1E-8),
				() -> assertMirrorInputs(reflected, circle, center));
	}

	@Test
	void pointOnSelectedCircleShouldBeChosenAsCenter() {
		GeoConic circle = add("c: (x - 1)^2 + (y + 2)^2 = 1");
		GeoPoint center = add("O = (2, -2)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -1);
		clickRW(2, -2);

		GeoConic reflected = getOnlyNewObject(existingObjects, GeoConic.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertAll(
				() -> assertEquals(3, reflected.getMidpoint().getX(), 1E-8),
				() -> assertEquals(-2, reflected.getMidpoint().getY(), 1E-8),
				() -> assertEquals(1, reflected.getCircleRadius(), 1E-8),
				() -> assertMirrorInputs(reflected, circle, center));
	}

	@Test
	void listOfPointsShouldReflectEachElement() {
		GeoList points = add("L = {(1, -1), (2, -2)}");
		GeoPoint first = assertInstanceOf(GeoPoint.class, points.get(0));
		GeoPoint second = assertInstanceOf(GeoPoint.class, points.get(1));
		points.setEuclidianVisible(true);
		points.updateRepaint();
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -1);
		clickRW(3, -3);

		GeoList reflected = getOnlyNewObject(existingObjects, GeoList.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertEquals(2, reflected.size());
		assertPoint(assertInstanceOf(GeoPoint.class, reflected.get(0)), 5, -5);
		assertPoint(assertInstanceOf(GeoPoint.class, reflected.get(1)), 4, -4);
		assertMirrorInputs(reflected, points, center);
		assertNotSame(first, reflected.get(0));
		assertNotSame(second, reflected.get(1));
		assertPoint(first, 1, -1);
		assertPoint(second, 2, -2);
		first.setCoords(2, -1, 1);
		points.updateCascade();
		assertPoint(assertInstanceOf(GeoPoint.class, reflected.get(0)), 4, -5);
		assertPoint(assertInstanceOf(GeoPoint.class, reflected.get(1)), 4, -4);
	}

	@Test
	void segmentThenPointShouldReflectEndpointsAndPreserveLength() {
		add("A = (1, -1)");
		add("B = (2, -2)");
		add("s = Segment(A, B)");
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1.5, -1.5);
		clickRW(3, -3);

		GeoSegment reflected = getOnlyNewObject(existingObjects, GeoSegment.class);
		assertOnlyNewObjects(
				existingObjects, reflected, reflected.getStartPoint(), reflected.getEndPoint());
		assertAll(
				() -> assertPoint(reflected.getStartPoint(), 5, -5),
				() -> assertPoint(reflected.getEndPoint(), 4, -4),
				() -> assertEquals(Math.sqrt(2), reflected.getLength(), 1E-8),
				() -> assertMirrorInputs(reflected.getStartPoint(), lookup("A"), center),
				() -> assertMirrorInputs(reflected.getEndPoint(), lookup("B"), center),
				() -> assertEquals(2, getNewObjects(existingObjects, GeoPoint.class).size()));
	}

	@Test
	void rayThenPointShouldReverseDirectionAndReflectStartPoint() {
		add("A = (1, -1)");
		add("B = (2, -2)");
		add("r = Ray(A, B)");
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1.5, -1.5);
		clickRW(3, -3);

		GeoRay reflected = getOnlyNewObject(existingObjects, GeoRay.class);
		GeoPoint reflectedEnd = assertInstanceOf(GeoPoint.class, lookup("B'"));
		assertOnlyNewObjects(existingObjects, reflected, reflected.getStartPoint(), reflectedEnd);
		assertAll(
				() -> assertPoint(reflected.getStartPoint(), 5, -5),
				() -> assertPoint(reflectedEnd, 4, -4),
				() -> assertMirrorInputs(reflected.getStartPoint(), lookup("A"), center),
				() -> assertMirrorInputs(reflectedEnd, lookup("B"), center),
				() -> assertSame(reflectedEnd, reflected.getParentAlgorithm().getInput()[1]),
				() -> assertTrue(reflected.isOnPath(new Coords(4, -4, 0, 1), 1E-8)),
				() -> assertTrue(reflected.isOnPath(new Coords(3, -3, 0, 1), 1E-8)),
				() -> assertFalse(reflected.isOnPath(new Coords(6, -6, 0, 1), 1E-8)));
	}

	@Test
	void vectorThenPointShouldNegateBothComponents() {
		GeoVector vector = add("u = Vector((1, -1), (2, -3))");
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1.5, -2);
		clickRW(3, -3);

		GeoVector reflected = getOnlyNewObject(existingObjects, GeoVector.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertAll(
				() -> assertTrue(reflected.isDefined()),
				() -> assertEquals(-1, reflected.getX(), 1E-8),
				() -> assertEquals(2, reflected.getY(), 1E-8),
				() -> assertMirrorInputs(reflected, vector, center));
	}

	@Test
	void functionThenPointShouldRemainFunction() {
		GeoFunction function = add("f(x) = x^2 - 4");
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -3);
		clickRW(3, -3);

		GeoFunction reflected = getOnlyNewObject(existingObjects, GeoFunction.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertAll(
				() -> assertTrue(reflected.isDefined()),
				() -> assertEquals(-6, reflected.value(4), 1E-8),
				() -> assertEquals(-3, reflected.value(5), 1E-8),
				() -> assertMirrorInputs(reflected, function, center));
	}

	@Test
	void polygonInteriorThenPointShouldReflectAllVertices() {
		GeoPolygon polygon = addTriangle();
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1.25, -1.5);
		clickRW(3, -3);

		GeoPolygon reflected = getOnlyNewObject(existingObjects, GeoPolygon.class);
		assertPolygonStructure(reflected, polygon, center);
		assertOnlyNewPolygon(existingObjects, reflected);
		assertAll(
				() -> assertPoint(reflected.getPoint(0), 5, -5),
				() -> assertPoint(reflected.getPoint(1), 4, -5),
				() -> assertPoint(reflected.getPoint(2), 5, -3),
				() -> assertEquals(1, reflected.getArea(), 1E-8));
		polygon.getPoint(1).setCoords(3, -1, 1);
		polygon.getPoint(1).updateCascade();
		assertPoint(reflected.getPoint(0), 5, -5);
		assertPoint(reflected.getPoint(1), 3, -5);
		assertPoint(reflected.getPoint(2), 5, -3);
		assertEquals(2, polygon.getArea(), 1E-8);
		assertEquals(2, reflected.getArea(), 1E-8);
		assertEquals(2, reflected.getSegments()[0].getLength(), 1E-8);
	}

	@Test
	void selectingPolygonAfterPointShouldReflectOnlyPolygon() {
		add("P = (4, -1)");
		GeoPolygon polygon = addTriangle();
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(4, -1);
		clickRW(1.25, -1.5);
		assertEquals(existingObjects, getObjectNames());
		clickRW(3, -3);

		GeoPolygon reflected = getOnlyNewObject(existingObjects, GeoPolygon.class);
		assertPolygonStructure(reflected, polygon, center);
		assertOnlyNewPolygon(existingObjects, reflected);
		assertAll(
				() -> assertPoint(reflected.getPoint(0), 5, -5),
				() -> assertPoint(reflected.getPoint(1), 4, -5),
				() -> assertPoint(reflected.getPoint(2), 5, -3));
	}

	@Test
	void rectangleSelectionShouldReflectPointsAndExcludeCenterItself() {
		add("A = (1, -1)");
		add("B = (2, -2)");
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		dragRW(0.5, -0.5, 3.5, -3.5);
		clickRW(3, -3);

		GeoPoint firstReflected = assertInstanceOf(GeoPoint.class, lookup("A'"));
		GeoPoint secondReflected = assertInstanceOf(GeoPoint.class, lookup("B'"));
		assertOnlyNewObjects(existingObjects, firstReflected, secondReflected);
		assertPoint(firstReflected, 5, -5);
		assertPoint(secondReflected, 4, -4);
		assertMirrorInputs(firstReflected, lookup("A"), center);
		assertMirrorInputs(secondReflected, lookup("B"), center);
	}

	@Test
	void rectangleAroundPolygonShouldNotDuplicateReflectedVertices() {
		GeoPolygon polygon = addTriangle();
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		dragRW(0.5, -0.5, 2.5, -3.5);
		clickRW(3, -3);

		GeoPolygon reflected = getOnlyNewObject(existingObjects, GeoPolygon.class);
		assertPolygonStructure(reflected, polygon, center);
		assertOnlyNewPolygon(existingObjects, reflected);
		assertAll(
				() -> assertPoint(reflected.getPoint(0), 5, -5),
				() -> assertPoint(reflected.getPoint(1), 4, -5),
				() -> assertPoint(reflected.getPoint(2), 5, -3));
	}

	@Test
	void firstPointShouldRemainSourceWhenCircleIsClickedNext() {
		GeoPoint point = add("A = (1, -2)");
		add("c: (x - 3)^2 + (y + 2)^2 = 1");
		GeoPoint center = add("O = (4, -4)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -2);
		clickRW(3, -1);
		assertEquals(existingObjects, getObjectNames());
		clickRW(4, -4);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, 7, -6);
		assertMirrorInputs(reflected, point, center);
	}

	@Test
	void clickingSourceAgainShouldSkipSelfReflectionAndClearSelection() {
		add("A = (1, -2)");
		GeoPoint point = add("B = (2, -1)");
		GeoPoint center = add("O = (3, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -2);
		resetMouseLocation();
		clickRW(1, -2);
		assertEquals(existingObjects, getObjectNames());
		clickRW(2, -1);
		assertEquals(existingObjects, getObjectNames());
		clickRW(3, -3);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, 4, -5);
		assertMirrorInputs(reflected, point, center);
	}

	@Test
	void hoveringShouldHighlightWithoutCreatingObjectsOrSelectingCenter() {
		GeoPoint point = add("A = (1, -2)");
		GeoPoint center = add("O = (3, -3)");
		GeoPoint otherPoint = add("B = (4, -1)");
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(1, -2);
		assertTrue(ec.getHighlightedgeos().contains(point));
		moveMouseRW(2, -4);
		assertEquals(existingObjects, getObjectNames());
		clickRW(1, -2);
		moveMouseRW(4, -1);
		assertTrue(ec.getHighlightedgeos().contains(otherPoint));
		assertEquals(existingObjects, getObjectNames());
		clickRW(3, -3);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, 5, -4);
		assertMirrorInputs(reflected, point, center);
	}

	@Test
	void toolShouldClearSelectionAfterCompletion() {
		add("A = (1, -2)");
		GeoPoint point = add("B = (2, -1)");
		add("O = (3, -3)");
		GeoPoint secondCenter = add("P = (4, -2)");

		clickRW(1, -2);
		clickRW(3, -3);
		Set<String> existingObjects = getObjectNames();
		clickRW(2, -1);
		assertEquals(existingObjects, getObjectNames());
		clickRW(4, -2);

		GeoPoint reflected = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertOnlyNewObjects(existingObjects, reflected);
		assertPoint(reflected, 6, -3);
		assertMirrorInputs(reflected, point, secondCenter);
	}

	private @NonNull GeoPolygon addTriangle() {
		add("A = (1, -1)");
		add("B = (2, -1)");
		add("C = (1, -3)");
		return add("p = Polygon(A, B, C)");
	}
}
