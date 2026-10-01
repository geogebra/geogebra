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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ATTACH_DETACH;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.test.TestEvent;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttachDetachToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_ATTACH_DETACH);
	}

	@Test
	void pointThenLineShouldAttachPointToLine() {
		add("A = (1, -1)");
		GeoLine line = add("g: y = -2");

		clickRW(1, -1);
		clickRW(2, -2);

		assertPointOnPath("A", line, 2, -2);
	}

	@Test
	void lineThenPointShouldAttachPointToLine() {
		add("A = (1, -1)");
		GeoLine line = add("g: y = -2");

		clickRW(2, -2);
		clickRW(1, -1);

		assertPointOnPath("A", line, 1, -2);
	}

	@Test
	void clickingEmptyViewShouldNotChangeConstruction() {
		add("A = (1, -1)");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -3);

		assertEquals(existingObjects, getObjectNames());
	}

	@Test
	void clickingPointOnPathShouldDetachIt() {
		add("g: y = -1");
		GeoPoint point = add("A = Point(g)");

		clickRW(point.getInhomX(), point.getInhomY());

		assertFreePoint("A");
	}

	@Test
	void clickingPointInRegionShouldDetachIt() {
		createSquare();
		GeoPoint point = add("E = PointIn(poly)");

		clickRW(point.getInhomX(), point.getInhomY());

		assertFreePoint("E");
	}

	@Test
	void pointThenPolygonInteriorShouldAttachPointToRegion() {
		add("E = (0, -2)");
		GeoPolygon polygon = createSquare();

		clickRW(0, -2);
		clickRW(3, -3);

		assertPointInRegion("E", polygon, 3, -3);
	}

	@Test
	void pointThenFilledConicInteriorShouldAttachPointToRegion() {
		add("A = (0, -1)");
		GeoConic conic = createFilledConic();

		clickRW(0, -1);
		clickRW(3, -3);

		assertPointInRegion("A", conic, 3, -3);
	}

	@Test
	void pointThenConicBoundaryShouldAttachPointToPath() {
		add("A = (0, -1)");
		GeoConic conic = createFilledConic();

		clickRW(0, -1);
		clickRW(5, -3);

		assertPointOnPath("A", conic, 5, -3);
	}

	@Test
	void pointThenInequalityShouldAttachPointToRegion() {
		add("A = (3, -1)");
		Region inequality = add("r: x + y < 0");

		clickRW(3, -1);
		clickRW(1, -2);

		assertPointInRegion("A", inequality, 1, -2);
	}

	@Test
	void altClickingConicBoundaryShouldForceRegionAttachment() {
		add("A = (0, -1)");
		GeoConic conic = createFilledConic();

		clickRW(0, -1);
		clickAltRW(5, -3);

		assertPointInRegion("A", conic, 5, -3);
	}

	@Test
	void draggingFreePointInEmptyViewShouldMoveIt() {
		add("A = (1, -1)");

		dragRW(1, -1, 2, -2);

		GeoPoint point = (GeoPoint) lookup("A");
		assertAll(
				() -> assertPointCoordinates(point, 2, -2),
				() -> assertFalse(point.isPointOnPath()),
				() -> assertFalse(point.isPointInRegion()));
	}

	@Test
	@Issue("APPS-6630")
	void draggingFreePointOntoLineShouldAttachIt() {
		add("A = (1, -1)");
		GeoLine line = add("g: y = 0");

		dragRW(1, -1, 1, 0);

		assertPointOnPath("A", line, 1, 0);
	}

	@Test
	void draggingPointAwayFromPathShouldDetachIt() {
		add("g: y = -1");
		GeoPoint point = add("A = Point(g)");

		dragThroughRW(point.getInhomX(), point.getInhomY(), point.getInhomX(), -2, 2, -3);

		GeoPoint detachedPoint = (GeoPoint) lookup("A");
		assertAll(
				() -> assertPointCoordinates(detachedPoint, 2, -3),
				() -> assertFalse(detachedPoint.isPointOnPath()),
				() -> assertFalse(detachedPoint.isPointInRegion()));
	}

	@Test
	void draggingPathPointShouldKeepItConstrainedToPath() {
		GeoLine line = add("g: y = -1");
		GeoPoint point = add("A = Point(g)");

		dragRW(point.getInhomX(), point.getInhomY(), 3, -3);

		assertPointOnPath("A", line, 3, -1);
	}

	@Test
	void draggingDependentPathPointOntoExistingPointShouldReplaceIt() {
		add("g: y = -1");
		GeoPoint point = add("A = Point(g)");
		GeoPoint target = add("B = (3, -3)");
		add("C = (0, -4)");
		add("h = Line(A, C)");

		dragThroughRW(point.getInhomX(), point.getInhomY(), point.getInhomX(), -2, 3, -3);

		GeoPoint replacement = (GeoPoint) lookup("B");
		GeoLine replacementLine = (GeoLine) lookup("h");
		assertAll(
				() -> assertNull(lookup("A")),
				() -> assertPointCoordinates(replacement, target.getInhomX(), target.getInhomY()),
				() -> assertTrue(replacementLine.isOnPath(replacement, 1E-8)));

		dragRW(3, -3, 4, -2);

		GeoPoint movedReplacement = (GeoPoint) lookup("B");
		GeoLine movedLine = (GeoLine) lookup("h");
		assertAll(
				() -> assertPointCoordinates(movedReplacement, 4, -2),
				() -> assertTrue(movedLine.isOnPath(movedReplacement, 1E-8)),
				() -> assertFalse(movedLine.isOnPath(target, 1E-8)));
	}

	@Test
	void draggingChildlessPathPointOntoExistingPointShouldNotReplaceIt() {
		add("g: y = -1");
		GeoPoint point = add("A = Point(g)");
		GeoPoint target = add("B = (3, -3)");

		dragThroughRW(point.getInhomX(), point.getInhomY(), point.getInhomX(), -2, 3, -3);

		GeoPoint detachedPoint = (GeoPoint) lookup("A");
		assertFreePoint("A");
		assertAll(
				() -> assertPointCoordinates(detachedPoint, 3, -3), () -> assertSame(target, lookup("B")));
	}

	@Test
	void pointShouldNotAttachToItsOwnChildPath() {
		GeoPoint point = add("A = (1, -1)");
		GeoConic circle = add("c = Circle(A, 2)");

		clickRW(1, -1);
		clickRW(3, -1);

		assertFreePoint("A");
		assertAll(
				() -> assertSame(point, lookup("A")),
				() -> assertSame(circle, lookup("c")),
				() -> assertPointCoordinates(point, 1, -1));
	}

	@Test
	void pointShouldNotAttachToItsOwnChildRegion() {
		GeoPoint point = add("A = (1, -1)");
		GeoConic conic = add("c = Circle(A, 2)");
		conic.setAlphaValue(1);
		conic.updateRepaint();

		clickRW(1, -1);
		clickAltRW(2, -1);

		assertFreePoint("A");
		assertAll(
				() -> assertSame(point, lookup("A")),
				() -> assertSame(conic, lookup("c")),
				() -> assertPointCoordinates(point, 1, -1));
	}

	private GeoPolygon createSquare() {
		add("A = (1, -1)");
		add("B = (5, -1)");
		add("C = (5, -5)");
		add("D = (1, -5)");
		return add("poly = Polygon(A, B, C, D)");
	}

	private GeoConic createFilledConic() {
		GeoConic conic = add("c: (x - 3)^2 + (y + 3)^2 = 4");
		conic.setAlphaValue(1);
		conic.updateRepaint();
		return conic;
	}

	private void clickAltRW(double xRW, double yRW) {
		EuclidianView view = getApp().getActiveEuclidianView();
		TestEvent event = new TestEvent(view.toScreenCoordX(xRW), view.toScreenCoordY(yRW)) {
			@Override
			public boolean isAltDown() {
				return true;
			}
		};
		ec.wrapMousePressed(event);
		ec.wrapMouseReleased(event);
	}

	private void dragThroughRW(
			double startXRW,
			double startYRW,
			double viaXRW,
			double viaYRW,
			double endXRW,
			double endYRW) {
		EuclidianView view = getApp().getActiveEuclidianView();
		dragStart(view.toScreenCoordX(startXRW), view.toScreenCoordY(startYRW));
		drag(view.toScreenCoordX(viaXRW), view.toScreenCoordY(viaYRW));
		dragEnd(view.toScreenCoordX(endXRW), view.toScreenCoordY(endYRW));
	}

	private void assertPointOnPath(
			String label, Path expectedPath, double expectedX, double expectedY) {
		GeoPoint point = (GeoPoint) lookup(label);
		assertAll(
				() -> assertPointCoordinates(point, expectedX, expectedY),
				() -> assertTrue(point.isPointOnPath()),
				() -> assertFalse(point.isPointInRegion()),
				() -> assertSame(expectedPath, point.getPath()));
	}

	private void assertPointInRegion(
			String label, Region expectedRegion, double expectedX, double expectedY) {
		GeoPoint point = (GeoPoint) lookup(label);
		assertAll(
				() -> assertPointCoordinates(point, expectedX, expectedY),
				() -> assertFalse(point.isPointOnPath()),
				() -> assertTrue(point.isPointInRegion()),
				() -> assertSame(expectedRegion, point.getRegion()));
	}

	private void assertFreePoint(String label) {
		GeoPoint point = (GeoPoint) lookup(label);
		assertAll(() -> assertFalse(point.isPointOnPath()), () -> assertFalse(point.isPointInRegion()));
	}

	private void assertPointCoordinates(GeoPoint point, double expectedX, double expectedY) {
		assertAll(
				() -> assertEquals(expectedX, point.getInhomX(), 1E-8),
				() -> assertEquals(expectedY, point.getInhomY(), 1E-8));
	}
}
