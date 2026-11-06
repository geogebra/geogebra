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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POLYGON;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PolygonToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_POLYGON);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@Test
	void newAndExistingVerticesShouldBeKeptInClickOrder() {
		GeoPoint first = add("A = (-2, 2)");
		GeoPoint second = add("B = (1, 3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(-2, 2);
		clickRW(1, 3);
		clickRW(2, 1);
		clickRW(-2, 2);

		GeoPoint third = assertInstanceOf(GeoPoint.class, lookup("C"));
		GeoPolygon polygon = getOnlyNewObject(existingObjects, GeoPolygon.class);
		assertPolygon(polygon, 3.5, first, second, third);
	}

	@Test
	void clickingFirstVertexShouldCreateClosedPolygon() {
		Set<String> existingObjects = getObjectNames();

		clickRW(-2, 0);
		clickRW(0, 0);
		clickRW(0, -3);
		assertTrue(getNewObjects(existingObjects, GeoPolygon.class).isEmpty());
		clickRW(-2, 0);

		List<GeoPoint> points = getNewObjects(existingObjects, GeoPoint.class);
		GeoPolygon polygon = getOnlyNewObject(existingObjects, GeoPolygon.class);
		assertAll(
				() -> assertEquals(List.of("(-2, 0)", "(0, 0)", "(0, -3)"), definedValueStrings(points)),
				() -> assertPolygon(polygon, 3, points.toArray(new GeoPoint[0])));
	}

	@Test
	void pointOnPathShouldRemainPolygonVertexAndUpdateArea() {
		GeoLine path = add("g: x = 2");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, 0);
		clickRW(0, 0);
		clickRW(0, -3);
		clickRW(2, 0);

		GeoPolygon polygon = getOnlyNewObject(existingObjects, GeoPolygon.class);
		GeoPoint pointOnPath = polygon.getPoint(0);
		assertAll(
				() -> assertTrue(pointOnPath.isPointOnPath()),
				() -> assertSame(path, pointOnPath.getPath()),
				() -> assertSame(pointOnPath, polygon.getPoint(0)),
				() -> assertEquals(3, polygon.getArea(), 1E-8));

		path.setCoords(1, 0, -4);
		path.updateCascade();

		assertAll(
				() -> assertEquals(4, pointOnPath.getInhomX(), 1E-8),
				() -> assertEquals(6, polygon.getArea(), 1E-8));
	}

	@Test
	void altClickShouldSnapFirstEdgeToFifteenDegrees() {
		Set<String> existingObjects = getObjectNames();
		clickRW(0, 0);

		altClickRW(2, -0.8);
		clickRW(2, -3);
		clickRW(0, 0);

		GeoPoint first = assertInstanceOf(GeoPoint.class, lookup("A"));
		GeoPoint snapped = assertInstanceOf(GeoPoint.class, lookup("B"));
		assertAll(
				() -> assertAngleIsMultipleOfFifteenDegrees(first, snapped),
				() -> assertFalse(
						Math.abs(snapped.getInhomX() - 2) < 1E-8 && Math.abs(snapped.getInhomY() + 0.8) < 1E-8),
				() -> assertEquals(1, getNewObjects(existingObjects, GeoPolygon.class).size()));
	}

	@Test
	void altClickShouldSnapNewVertexRelativeToFirstAndPreviousVertices() {
		// Capture existing objects before creating either polygon.
		Set<String> existingObjects = getObjectNames();
		// Keep the snapshot close to its use to satisfy PMD.
		createPolygonWithAltClick();

		GeoPoint first = assertInstanceOf(GeoPoint.class, lookup("A"));
		GeoPoint previous = assertInstanceOf(GeoPoint.class, lookup("B"));
		GeoPoint snapped = assertInstanceOf(GeoPoint.class, lookup("C"));
		assertAll(
				() -> assertEquals(1, getNewObjects(existingObjects, GeoPolygon.class).size()),
				() -> assertAngleIsMultipleOfFifteenDegrees(previous, snapped),
				() -> assertAngleIsMultipleOfFifteenDegrees(first, snapped),
				() -> assertFalse(
						Math.abs(snapped.getInhomX() - 4) < 1E-8 && Math.abs(snapped.getInhomY() + 1) < 1E-8));
	}

	private void createPolygonWithAltClick() {
		clickRW(0, 0);
		clickRW(2, 0);

		altClickRW(4, -1);
		moveMouseRW(0, 0);
		clickRW(0, 0);
	}

	@Test
	void hoveringFirstVertexShouldNotFinishPolygon() {
		clickRW(-2, 0);
		clickRW(0, 0);
		clickRW(0, -3);
		GeoPoint first = assertInstanceOf(GeoPoint.class, lookup("A"));
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(-2, 0);

		assertAll(
				() -> assertFalse(ec.getHighlightedgeos().contains(first)),
				() -> assertTrue(getNewObjects(existingObjects, GeoPolygon.class).isEmpty()));
	}

	@Test
	void hoveringEmptySpaceShouldNotCreateVertex() {
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(3, -3);

		assertEquals(existingObjects, getObjectNames());
	}

	@Test
	void toolShouldCreateAnotherPolygonAfterCompletion() {
		// Capture existing objects before creating either polygon.
		Set<String> existingObjects = getObjectNames();
		// Keep the snapshot close to its use to satisfy PMD.
		createTwoPolygons();
		List<GeoPolygon> polygons = getNewObjects(existingObjects, GeoPolygon.class);
		assertEquals(2, polygons.size());
		polygons.forEach(polygon -> assertAll(
				() -> assertEquals(3, polygon.getPointsLength()),
				() -> assertEquals(3, polygon.getSegments().length),
				() -> assertEquals(0.5, polygon.getArea(), 1E-8)));
	}

	private void createTwoPolygons() {
		clickRW(-3, 3);
		clickRW(-2, 3);
		clickRW(-2, 2);
		clickRW(-3, 3);
		clickRW(1, 1);
		clickRW(2, 1);
		clickRW(2, 0);
		clickRW(1, 1);
	}

	private void assertPolygon(GeoPolygon polygon, double expectedArea, GeoPoint... expectedPoints) {
		assertEquals(expectedPoints.length, polygon.getPointsLength());
		GeoSegmentND[] segments = polygon.getSegments();
		assertEquals(expectedPoints.length, segments.length);
		for (int index = 0; index < expectedPoints.length; index++) {
			int nextIndex = (index + 1) % expectedPoints.length;
			assertSame(expectedPoints[index], polygon.getPoint(index));
			assertSame(expectedPoints[index], segments[index].getStartPoint());
			assertSame(expectedPoints[nextIndex], segments[index].getEndPoint());
		}
		assertEquals(expectedArea, polygon.getArea(), 1E-8);
	}

	private void assertAngleIsMultipleOfFifteenDegrees(GeoPoint start, GeoPoint end) {
		double angle = Math.toDegrees(
				Math.atan2(end.getInhomY() - start.getInhomY(), end.getInhomX() - start.getInhomX()));
		double multiplesOfFifteen = angle / 15;
		assertEquals(Math.rint(multiplesOfFifteen), multiplesOfFifteen, 1E-8);
	}

	private void altClickRW(double xRW, double yRW) {
		EuclidianView view = getApp().getActiveEuclidianView();
		TestEvent altEvent = new TestEvent(view.toScreenCoordX(xRW), view.toScreenCoordY(yRW)) {
			@Override
			public boolean isAltDown() {
				return true;
			}
		};
		ec.wrapMouseMoved(altEvent);
		ec.wrapMousePressed(altEvent);
		ec.wrapMouseReleased(altEvent);
	}
}
