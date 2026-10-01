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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_COMPLEX_NUMBER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComplexNumberToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_COMPLEX_NUMBER);
	}

	@Test
	void clickingEmptyViewShouldCreateComplexNumber() {
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		GeoPoint point = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertAll(
				() -> assertComplexPoint(point, 2, -2),
				() -> assertEquals("z_{1}", point.getLabelSimple()),
				() -> assertTrue(point.isIndependent()),
				() -> assertFalse(point.isPointOnPath()),
				() -> assertFalse(point.isPointInRegion()));
	}

	@Test
	void draggingEmptyViewShouldCreateComplexNumberAtReleasePosition() {
		Set<String> existingObjects = getObjectNames();

		dragRW(1, -1, 2, -2);

		GeoPoint point = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertAll(
				() -> assertComplexPoint(point, 2, -2),
				() -> assertTrue(point.isIndependent()),
				() -> assertFalse(point.isPointOnPath()),
				() -> assertFalse(point.isPointInRegion()));
	}

	@Test
	void clickingExistingPointShouldNotCreateOrConvertIt() {
		GeoPoint point = add("A = (2, -2)");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		assertAll(
				() -> assertEquals(existingObjects, getObjectNames()),
				() -> assertFalse(GeoPoint.isComplexNumber(point)));
	}

	@Test
	void clickingLineShouldCreateComplexNumberOnPath() {
		GeoLine line = add("g: y = -1");
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -1);

		assertComplexPointOnPath(existingObjects, 2, -1, line);
	}

	@Test
	void draggingFromLineShouldCreateConstrainedComplexNumberAtReleasePosition() {
		GeoLine line = add("g: y = -1");
		Set<String> existingObjects = getObjectNames();

		dragRW(1, -1, 3, -3);

		assertComplexPointOnPath(existingObjects, 3, -1, line);
	}

	@Test
	void draggingExistingComplexNumberShouldMoveIt() {
		Set<String> existingObjects = getObjectNames();
		clickRW(1, -1);
		GeoPoint point = getOnlyNewObject(existingObjects, GeoPoint.class);
		Set<String> objectsAfterCreation = getObjectNames();

		resetMouseLocation();
		dragRW(1, -1, 2, -2);

		assertAll(
				() -> assertEquals(objectsAfterCreation, getObjectNames()),
				() -> assertSame(point, lookup(point.getLabelSimple())),
				() -> assertComplexPoint(point, 2, -2));
	}

	@Test
	void clickingInsidePolygonShouldCreateFreeComplexNumber() {
		GeoPolygon polygon = createSquare();
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(3, -3);
		assertTrue(getApp().getActiveEuclidianView().getHits().contains(polygon));
		clickRW(3, -3);

		assertFreeComplexPoint(existingObjects, 3, -3);
	}

	@Test
	void clickingPolygonEdgeShouldCreateComplexNumberOnEdge() {
		Path edge = createSquare().getSegments()[0];
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -1);

		assertComplexPointOnPath(existingObjects, 3, -1, edge);
	}

	@Test
	void clickingInsideFilledConicShouldCreateFreeComplexNumber() {
		GeoConic conic = createFilledCircle();
		Set<String> existingObjects = getObjectNames();

		moveMouseRW(3, -3);
		assertTrue(getApp().getActiveEuclidianView().getHits().contains(conic));
		clickRW(3, -3);

		assertFreeComplexPoint(existingObjects, 3, -3);
	}

	@Test
	void clickingConicBoundaryShouldCreateComplexNumberOnConic() {
		GeoConic conic = createFilledCircle();
		Set<String> existingObjects = getObjectNames();

		clickRW(5, -3);

		assertComplexPointOnPath(existingObjects, 5, -3, conic);
	}

	private GeoPolygon createSquare() {
		add("A = (1, -1)");
		add("B = (5, -1)");
		add("C = (5, -5)");
		add("D = (1, -5)");
		return add("p = Polygon(A, B, C, D)");
	}

	private GeoConic createFilledCircle() {
		GeoConic conic = add("c: (x - 3)^2 + (y + 3)^2 = 4");
		conic.setAlphaValue(1);
		conic.updateRepaint();
		return conic;
	}

	private void assertComplexPointOnPath(
			Set<String> existingObjects, double expectedX, double expectedY, Path expectedPath) {
		GeoPoint point = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertAll(
				() -> assertComplexPoint(point, expectedX, expectedY),
				() -> assertTrue(point.isPointOnPath()),
				() -> assertFalse(point.isPointInRegion()),
				() -> assertSame(expectedPath, point.getPath()));
	}

	private void assertFreeComplexPoint(
			Set<String> existingObjects, double expectedX, double expectedY) {
		GeoPoint point = getOnlyNewObject(existingObjects, GeoPoint.class);
		assertAll(
				() -> assertComplexPoint(point, expectedX, expectedY),
				() -> assertTrue(point.isIndependent()),
				() -> assertFalse(point.isPointOnPath()),
				() -> assertFalse(point.isPointInRegion()));
	}

	private void assertComplexPoint(GeoPoint point, double expectedX, double expectedY) {
		assertAll(
				() -> assertEquals(expectedX, point.getInhomX(), 1E-8),
				() -> assertEquals(expectedY, point.getInhomY(), 1E-8),
				() -> assertTrue(GeoPoint.isComplexNumber(point)));
	}
}
