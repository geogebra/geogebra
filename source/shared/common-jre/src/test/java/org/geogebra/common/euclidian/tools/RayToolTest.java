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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_RAY;
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
import org.geogebra.common.kernel.geos.GeoRay;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RayToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_RAY);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@Test
	void existingPointThenEmptySpaceShouldCreateRay() {
		GeoPoint startPoint = add("A = (-1, 1)");

		clickRW(-1, 1);
		clickRW(2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1");
		assertRayDirection(startPoint, 5, -3, -4, 3);
	}

	@Test
	void clickingExistingPointsInReverseOrderShouldReverseRay() {
		add("A = (-1, 1)");
		GeoPoint startPoint = add("B = (2, -1)");

		clickRW(2, -1);
		clickRW(-1, 1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f: -2x - 3y = -1");
		assertRayDirection(startPoint, -4, 3, 5, -3);
	}

	@Test
	void emptySpaceThenExistingPointShouldCreateRay() {
		add("A = (2, -1)");

		clickRW(-1, 1);
		clickRW(2, -1);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("B"));
		checkContent("A = (2, -1)", "B = (-1, 1)", "f: 2x + 3y = 1");
		assertRayDirection(startPoint, 5, -3, -4, 3);
	}

	@Test
	void twoEmptySpaceClicksShouldCreatePointsAndRay() {
		clickRW(-1, 1);
		clickRW(2, -1);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1");
		assertRayDirection(startPoint, 5, -3, -4, 3);
	}

	@Test
	void firstPointAloneShouldNotCreateRay() {
		clickRW(-1, 1);

		checkContent("A = (-1, 1)");
	}

	@Test
	void clickingSamePointTwiceShouldNotCreateRay() {
		add("A = (-1, 1)");

		clickRW(-1, 1);
		clickRW(-1, 1);

		checkContent("A = (-1, 1)");
	}

	@Test
	void pointOnPathShouldBeUsedAsRayOrigin() {
		GeoLine path = add("g: y = -1");

		clickRW(2, -1);
		clickRW(-1, 1);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertTrue(startPoint.isPointOnPath());

		path.setCoords(0, 1, 2);
		path.updateCascade();

		checkContent("g: y = -2", "A = (2, -2)", "B = (-1, 1)", "f: -x - y = 0");
		assertRayDirection(startPoint, -4, 4, 5, -5);
	}

	@Test
	void intersectionPointShouldBeUsedAsRayOrigin() {
		GeoLine horizontalLine = add("g: y = -1");
		GeoLine verticalLine = add("h: x = 2");

		clickRW(2, -1);
		clickRW(-1, 1);

		GeoPoint intersection = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertFalse(intersection.isIndependent());

		horizontalLine.setCoords(0, 1, 2);
		horizontalLine.updateCascade();
		verticalLine.setCoords(1, 0, -3);
		verticalLine.updateCascade();

		checkContent("g: y = -2", "h: x = 3", "A = (3, -2)", "B = (-1, 1)", "f: -3x - 4y = -1");
		assertRayDirection(intersection, -5, 4, 7, -5);
	}

	@Test
	void clickingPolygonFillShouldCreateFreeOrigin() {
		add("A = (-2, 2)");
		add("B = (2, 2)");
		add("C = (2, -2)");
		add("D = (-2, -2)");
		add("p = Polygon(A, B, C, D)");
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 0);
		clickRW(3, -3);

		List<GeoPoint> points = getNewObjects(existingObjects, GeoPoint.class);
		assertEquals(List.of("(0, 0)", "(3, -3)"), definedValueStrings(points));
		assertTrue(points.stream().allMatch(GeoPoint::isIndependent));
		GeoRay ray = getOnlyNewObject(existingObjects, GeoRay.class);
		assertRayDirection(ray, points.get(0), 6, -6, -3, 3);
	}

	@Test
	void altClickShouldSnapDirectionToFifteenDegrees() {
		clickRW(0, 0);
		EuclidianView view = getApp().getActiveEuclidianView();
		TestEvent altEvent = new TestEvent(view.toScreenCoordX(2), view.toScreenCoordY(-1)) {
			@Override
			public boolean isAltDown() {
				return true;
			}
		};

		ec.wrapMouseMoved(altEvent);
		ec.wrapMousePressed(altEvent);
		ec.wrapMouseReleased(altEvent);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		GeoPoint endpoint = assertInstanceOf(GeoPoint.class, lookup("B"));
		GeoRay ray = assertInstanceOf(GeoRay.class, lookup("f"));
		assertSame(startPoint, ray.getStartPoint());
		assertEquals(-Math.PI / 6, Math.atan2(endpoint.getInhomY(), endpoint.getInhomX()), 1E-8);
		assertEquals(Math.sqrt(5), endpoint.distance(ray.getStartPoint()), 1E-8);
	}

	@Test
	void draggingBetweenEmptyLocationsShouldCreatePointsAndRay() {
		dragRW(-1, 1, 2, -1);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1");
		assertRayDirection(startPoint, 5, -3, -4, 3);
	}

	@Test
	void draggingFromExistingPointShouldCreateSecondPointAndRay() {
		GeoPoint startPoint = add("A = (-1, 1)");

		dragRW(-1, 1, 2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1");
		assertRayDirection(startPoint, 5, -3, -4, 3);
	}

	@Test
	void draggingToExistingPointShouldCreateFirstPointAndRay() {
		add("A = (2, -1)");

		dragRW(-1, 1, 2, -1);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("B"));
		checkContent("A = (2, -1)", "B = (-1, 1)", "f: 2x + 3y = 1");
		assertRayDirection(startPoint, 5, -3, -4, 3);
	}

	@Test
	void draggingBetweenExistingPointsShouldCreateRay() {
		GeoPoint startPoint = add("A = (-1, 1)");
		add("B = (2, -1)");

		dragRW(-1, 1, 2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1");
		assertRayDirection(startPoint, 5, -3, -4, 3);
	}

	@Test
	void releasingNewSecondPointAwayFromPressShouldUseReleasePosition() {
		clickRW(0, 0);
		pressRW(2, -2);
		releaseRW(4, -3);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		checkContent("A = (0, 0)", "B = (4, -3)", "f: 3x + 4y = 0");
		assertRayDirection(startPoint, 8, -6, -4, 3);
	}

	@Test
	void releasingExistingSecondPointAwayFromPressShouldMoveIt() {
		add("A = (2, -2)");
		clickRW(0, 0);

		pressRW(2, -2);
		releaseRW(4, -3);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("B"));
		checkContent("A = (4, -3)", "B = (0, 0)", "f: 3x + 4y = 0");
		assertRayDirection(startPoint, 8, -6, -4, 3);
	}

	@Test
	void toolShouldCreateAnotherRayAfterCompletion() {
		dragRW(-1, 1, 2, -1);
		dragRW(-1, 1, 3, -2);

		checkContent("A = (-1, 1)", "B = (2, -1)", "f: 2x + 3y = 1", "C = (3, -2)", "g: 3x + 4y = 1");
		GeoRay secondRay = assertInstanceOf(GeoRay.class, lookup("g"));
		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertRayDirection(secondRay, startPoint, 7, -5, -5, 4);
	}

	private GeoRay assertRayDirection(
			GeoPoint expectedStart,
			double forwardX,
			double forwardY,
			double backwardX,
			double backwardY) {
		GeoRay ray = assertInstanceOf(GeoRay.class, lookup("f"));
		return assertRayDirection(ray, expectedStart, forwardX, forwardY, backwardX, backwardY);
	}

	private GeoRay assertRayDirection(
			GeoRay ray,
			GeoPoint expectedStart,
			double forwardX,
			double forwardY,
			double backwardX,
			double backwardY) {
		assertAll(
				() -> assertSame(expectedStart, ray.getStartPoint()),
				() -> assertTrue(ray.isOnPath(new Coords(forwardX, forwardY, 1), 1E-8)),
				() -> assertFalse(ray.isOnPath(new Coords(backwardX, backwardY, 1), 1E-8)));
		return ray;
	}
}
