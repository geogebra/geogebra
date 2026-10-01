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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POINT_ON_OBJECT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PointOnObjectToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_POINT_ON_OBJECT);
	}

	@Test
	void clickingLineShouldCreatePointOnLine() {
		GeoLine line = add("g: y = -1");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -1);

		assertPointOnPath(existingObjects, 2, -1, line);
	}

	@Test
	void clickingInsidePolygonShouldCreatePointInRegion() {
		GeoPolygon polygon = createSquare();
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -3);

		assertPointInRegion(existingObjects, 3, -3, polygon);
	}

	@Test
	void pathShouldTakePriorityOverOverlappingRegion() {
		GeoPolygon polygon = createSquare();
		GeoLine line = add("g: y = -3");
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(3, -3);
		assertAll(
				() -> assertTrue(getApp().getActiveEuclidianView().getHits().contains(polygon)),
				() -> assertTrue(getApp().getActiveEuclidianView().getHits().contains(line)));
		clickRW(3, -3);

		assertPointOnPath(existingObjects, 3, -3, line);
	}

	@Test
	void clickingPolygonEdgeShouldCreatePointOnPolygonBoundary() {
		GeoPolygon polygon = createSquare();
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -1);

		assertPointOnPath(existingObjects, 3, -1, polygon);
	}

	@Test
	void clickingInsideConicShouldCreatePointInRegion() {
		GeoConic conic = add("c: (x - 3)^2 + (y + 3)^2 = 4");
		conic.setAlphaValue(1);
		conic.updateRepaint();
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -3);

		assertPointInRegion(existingObjects, 3, -3, conic);
	}

	@Test
	void clickingConicBoundaryShouldCreatePointOnConic() {
		GeoConic conic = add("c: (x - 3)^2 + (y + 3)^2 = 4");
		conic.setAlphaValue(1);
		conic.updateRepaint();
		Set<String> existingObjects = getObjectNames();

		clickRW(5, -3);

		assertPointOnPath(existingObjects, 5, -3, conic);
	}

	@Test
	void clickingInsideOneVariableInequalityShouldCreatePointInRegion() {
		GeoFunction inequality = add("r: x < 4");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -3);

		assertPointInRegion(existingObjects, 2, -3, inequality);
	}

	@Test
	void clickingInsideTwoVariableInequalityShouldCreatePointInRegion() {
		GeoFunctionNVar inequality = add("r: x + y < 0");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -3);

		assertPointInRegion(existingObjects, 2, -3, inequality);
	}

	@Test
	void clickingIntersectionShouldCreateIntersectionPoint() {
		GeoLine firstLine = add("g: y = -1");
		GeoLine secondLine = add("h: x = 2");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -1);

		GeoPoint point = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertAll(
				() -> assertPointCoordinates(point, 2, -1),
				() -> assertTrue(firstLine.isParentOf(point)),
				() -> assertTrue(secondLine.isParentOf(point)));
	}

	@Test
	void clickingExistingPointShouldNotCreateDuplicate() {
		add("A = (2, -2)");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		assertEquals(existingObjects, getObjectNames());
	}

	@Test
	void clickingEmptyViewShouldCreateFreePoint() {
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		GeoPoint point = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertAll(
				() -> assertPointCoordinates(point, 2, -2),
				() -> assertFalse(point.isPointOnPath()),
				() -> assertFalse(point.isPointInRegion()));
	}

	@Test
	void draggingPointOnLineShouldKeepItConstrained() {
		GeoLine line = add("g: y = -1");
		Set<String> existingObjects = getObjectNames();

		dragRW(1, -1, 3, -3);

		assertPointOnPath(existingObjects, 3, -1, line);
	}

	private GeoPolygon createSquare() {
		add("A = (1, -1)");
		add("B = (5, -1)");
		add("C = (5, -5)");
		add("D = (1, -5)");
		return add("p = Polygon(A, B, C, D)");
	}

	private void assertPointOnPath(
			Set<String> existingObjects, double expectedX, double expectedY, Path expectedPath) {
		GeoPoint point = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertAll(
				() -> assertPointCoordinates(point, expectedX, expectedY),
				() -> assertTrue(point.isPointOnPath()),
				() -> assertFalse(point.isPointInRegion()),
				() -> assertSame(expectedPath, point.getPath()));
	}

	private void assertPointInRegion(
			Set<String> existingObjects, double expectedX, double expectedY, Region expectedRegion) {
		GeoPoint point = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertAll(
				() -> assertPointCoordinates(point, expectedX, expectedY),
				() -> assertFalse(point.isPointOnPath()),
				() -> assertTrue(point.isPointInRegion()),
				() -> assertSame(expectedRegion, point.getRegion()));
	}

	private void assertPointCoordinates(GeoPoint point, double expectedX, double expectedY) {
		assertAll(
				() -> assertEquals(expectedX, point.getInhomX(), 1E-8),
				() -> assertEquals(expectedY, point.getInhomY(), 1E-8));
	}
}
