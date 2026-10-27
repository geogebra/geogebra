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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POLYLINE;
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
import org.geogebra.common.kernel.geos.GeoPolyLine;
import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PolyLineToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_POLYLINE);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@Test
	void clickingFirstVertexShouldFinishOpenPolyline() {
		Set<String> existingObjects = getObjectNames();

		clickRW(-2, 0);
		clickRW(0, 0);
		clickRW(0, -3);
		assertTrue(getNewObjects(existingObjects, GeoPolyLine.class).isEmpty());
		clickRW(-2, 0);

		List<GeoPoint> points = getNewObjects(existingObjects, GeoPoint.class);
		GeoPolyLine polyline = getOnlyNewObject(existingObjects, GeoPolyLine.class);
		assertAll(
				() -> assertEquals(List.of("(-2, 0)", "(0, 0)", "(0, -3)"), definedValueStrings(points)),
				() -> assertPolyline(polyline, 5, points.toArray(new GeoPoint[0])));
	}

	@Test
	void existingVerticesShouldBeKeptInClickOrder() {
		GeoPoint first = add("A = (-2, 0)");
		GeoPoint second = add("B = (0, -3)");
		GeoPoint third = add("C = (0, 0)");
		Set<String> existingObjects = getObjectNames();

		clickRW(-2, 0);
		clickRW(0, 0);
		clickRW(0, -3);
		clickRW(-2, 0);

		assertPolyline(getOnlyNewObject(existingObjects, GeoPolyLine.class), 5, first, third, second);
	}

	@Test
	void newAndExistingVerticesShouldBeCombinedInClickOrder() {
		GeoPoint existingPoint = add("M = (0, 0)");
		Set<String> existingObjects = getObjectNames();

		clickRW(-2, 0);
		clickRW(0, 0);
		clickRW(0, -3);
		clickRW(-2, 0);

		List<GeoPoint> newPoints = getNewObjects(existingObjects, GeoPoint.class);
		GeoPolyLine polyline = getOnlyNewObject(existingObjects, GeoPolyLine.class);
		assertAll(
				() -> assertEquals(List.of("(-2, 0)", "(0, -3)"), definedValueStrings(newPoints)),
				() -> assertPolyline(polyline, 5, newPoints.get(0), existingPoint, newPoints.get(1)));
	}

	@Test
	void clickingFirstVertexWithOnlyTwoVerticesShouldNotFinish() {
		GeoPoint first = add("A = (-2, 0)");
		GeoPoint second = add("B = (0, 0)");
		Set<String> existingObjects = getObjectNames();

		clickRW(-2, 0);
		clickRW(0, 0);
		clickRW(-2, 0);

		assertAll(
				() -> assertTrue(getNewObjects(existingObjects, GeoPolyLine.class).isEmpty()),
				() -> assertFalse(getApp().getSelectionManager().containsSelectedGeo(first)),
				() -> assertTrue(getApp().getSelectionManager().containsSelectedGeo(second)));
	}

	@Test
	void clickingNonFirstVertexShouldNotFinishPolyline() {
		GeoPoint first = add("A = (-2, 0)");
		GeoPoint second = add("B = (0, 0)");
		GeoPoint third = add("C = (0, -3)");
		Set<String> existingObjects = getObjectNames();

		clickRW(-2, 0);
		clickRW(0, 0);
		clickRW(0, -3);
		clickRW(0, 0);

		assertAll(
				() -> assertTrue(getNewObjects(existingObjects, GeoPolyLine.class).isEmpty()),
				() -> assertTrue(getApp().getSelectionManager().containsSelectedGeo(first)),
				() -> assertFalse(getApp().getSelectionManager().containsSelectedGeo(second)),
				() -> assertTrue(getApp().getSelectionManager().containsSelectedGeo(third)));
	}

	@Test
	void pointOnPathShouldRemainPolylineVertex() {
		GeoLine path = add("g: y = -1");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -1);
		clickRW(0, 0);
		clickRW(0, 2);
		clickRW(2, -1);

		GeoPoint pointOnPath = getNewObjects(existingObjects, GeoPoint.class).get(0);
		GeoPolyLine polyline = getOnlyNewObject(existingObjects, GeoPolyLine.class);
		assertAll(
				() -> assertTrue(pointOnPath.isPointOnPath()),
				() -> assertSame(path, pointOnPath.getPath()),
				() -> assertSame(pointOnPath, polyline.getPointND(0)),
				() -> assertEquals(Math.sqrt(5) + 2, polyline.getLength(), 1E-8));

		path.setCoords(0, 1, 2);
		path.updateCascade();

		assertEquals(2 * Math.sqrt(2) + 2, polyline.getLength(), 1E-8);
	}

	@Test
	void intersectionShouldRemainPolylineVertex() {
		GeoLine horizontalLine = add("g: y = -1");
		GeoLine verticalLine = add("h: x = 2");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -1);
		clickRW(0, 0);
		clickRW(0, 2);
		clickRW(2, -1);

		GeoPoint intersection = getNewObjects(existingObjects, GeoPoint.class).get(0);
		GeoPolyLine polyline = getOnlyNewObject(existingObjects, GeoPolyLine.class);
		assertAll(
				() -> assertFalse(intersection.isIndependent()),
				() -> assertSame(intersection, polyline.getPointND(0)));

		horizontalLine.setCoords(0, 1, 2);
		horizontalLine.updateCascade();
		verticalLine.setCoords(1, 0, -3);
		verticalLine.updateCascade();

		assertAll(
				() -> assertEquals(3, intersection.getInhomX(), 1E-8),
				() -> assertEquals(-2, intersection.getInhomY(), 1E-8),
				() -> assertEquals(Math.sqrt(13) + 2, polyline.getLength(), 1E-8));
	}

	@Test
	void clickingPolygonFillShouldCreateFreeVertex() {
		add("A = (-2, 2)");
		add("B = (2, 2)");
		add("C = (2, -2)");
		add("D = (-2, -2)");
		add("p = Polygon(A, B, C, D)");
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 0);
		clickRW(3, 0);
		clickRW(3, -3);
		clickRW(0, 0);

		List<GeoPoint> vertices = getNewObjects(existingObjects, GeoPoint.class);
		GeoPolyLine polyline = getOnlyNewObject(existingObjects, GeoPolyLine.class);
		assertAll(
				() -> assertEquals(List.of("(0, 0)", "(3, 0)", "(3, -3)"), definedValueStrings(vertices)),
				() -> assertTrue(vertices.stream().allMatch(GeoPoint::isIndependent)),
				() -> assertPolyline(polyline, 6, vertices.toArray(new GeoPoint[0])));
	}

	@Test
	void altClickShouldSnapLastSegmentToFifteenDegrees() {
		clickRW(0, 0);
		clickRW(2, 0);
		EuclidianView view = getApp().getActiveEuclidianView();
		TestEvent altEvent = new TestEvent(view.toScreenCoordX(4), view.toScreenCoordY(-1)) {
			@Override
			public boolean isAltDown() {
				return true;
			}
		};

		ec.wrapMouseMoved(altEvent);
		ec.wrapMousePressed(altEvent);
		ec.wrapMouseReleased(altEvent);
		moveMouseRW(0, 0);
		clickRW(0, 0);

		GeoPoint second = assertInstanceOf(GeoPoint.class, lookup("B"));
		GeoPoint snapped = assertInstanceOf(GeoPoint.class, lookup("C"));
		GeoPolyLine polyline = assertInstanceOf(GeoPolyLine.class, lookup("f"));
		assertAll(
				() -> assertEquals(
						-Math.PI / 6,
						Math.atan2(
								snapped.getInhomY() - second.getInhomY(), snapped.getInhomX() - second.getInhomX()),
						1E-8),
				() -> assertEquals(2 + Math.sqrt(5), polyline.getLength(), 1E-8));
	}

	@Test
	void draggingShouldPlaceOneVertexAtReleasePosition() {
		Set<String> existingObjects = getObjectNames();

		dragRW(-2, 0, 0, 0);

		List<GeoPoint> points = getNewObjects(existingObjects, GeoPoint.class);
		assertAll(
				() -> assertEquals(List.of("(0, 0)"), definedValueStrings(points)),
				() -> assertTrue(getNewObjects(existingObjects, GeoPolyLine.class).isEmpty()));

		clickRW(2, 0);
		clickRW(2, -2);
		clickRW(0, 0);

		List<GeoPoint> vertices = getNewObjects(existingObjects, GeoPoint.class);
		assertPolyline(
				getOnlyNewObject(existingObjects, GeoPolyLine.class), 4, vertices.toArray(new GeoPoint[0]));
	}

	@Test
	void hoveringFirstVertexShouldNotFinishPolyline() {
		clickRW(-2, 0);
		clickRW(0, 0);
		clickRW(0, -3);
		GeoPoint first = assertInstanceOf(GeoPoint.class, lookup("A"));
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(-2, 0);

		assertAll(
				() -> assertFalse(ec.getHighlightedgeos().contains(first)),
				() -> assertTrue(getNewObjects(existingObjects, GeoPolyLine.class).isEmpty()));
	}

	@Test
	void hoveringEmptySpaceShouldNotCreateVertex() {
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(3, -3);

		assertEquals(existingObjects, getObjectNames());
	}

	@Test
	void toolShouldCreateAnotherPolylineAfterCompletion() {
		final Set<String> existingObjects = getObjectNames();

		clickRW(-3, 3);
		clickRW(-2, 3);
		clickRW(-2, 2);
		clickRW(-3, 3);
		clickRW(1, 1);
		clickRW(2, 1);
		clickRW(2, 0);
		clickRW(1, 1);

		List<GeoPolyLine> polylines = getNewObjects(existingObjects, GeoPolyLine.class);
		assertEquals(2, polylines.size());
		polylines.forEach(polyline -> assertEquals(2, polyline.getLength(), 1E-8));
	}

	private void assertPolyline(
			GeoPolyLine polyline, double expectedLength, GeoPoint... expectedPoints) {
		assertEquals(expectedPoints.length, polyline.getNumPoints());
		for (int index = 0; index < expectedPoints.length; index++) {
			assertSame(expectedPoints[index], polyline.getPointND(index));
		}
		assertEquals(expectedLength, polyline.getLength(), 1E-8);
	}
}
