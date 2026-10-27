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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VECTOR;
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
import org.geogebra.common.kernel.geos.GeoVector;
import org.geogebra.test.TestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VectorToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_VECTOR);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@Test
	void existingPointThenEmptySpaceShouldCreateVector() {
		GeoPoint startPoint = add("A = (-1, 1)");

		clickRW(-1, 1);
		clickRW(2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "u = (3, -2)");
		assertVector("u", startPoint, 3, -2);
	}

	@Test
	void clickingExistingPointsInReverseOrderShouldReverseVector() {
		add("A = (-1, 1)");
		GeoPoint startPoint = add("B = (2, -1)");

		clickRW(2, -1);
		clickRW(-1, 1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "u = (-3, 2)");
		assertVector("u", startPoint, -3, 2);
	}

	@Test
	void emptySpaceThenExistingPointShouldCreateVector() {
		add("A = (2, -1)");

		clickRW(-1, 1);
		clickRW(2, -1);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("B"));
		checkContent("A = (2, -1)", "B = (-1, 1)", "u = (3, -2)");
		assertVector("u", startPoint, 3, -2);
	}

	@Test
	void twoEmptySpaceClicksShouldCreatePointsAndVector() {
		clickRW(-1, 1);
		clickRW(2, -1);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		checkContent("A = (-1, 1)", "B = (2, -1)", "u = (3, -2)");
		assertVector("u", startPoint, 3, -2);
	}

	@Test
	void firstPointAloneShouldNotCreateVector() {
		clickRW(-1, 1);

		checkContent("A = (-1, 1)");
	}

	@Test
	void clickingSamePointTwiceShouldNotCreateVector() {
		add("A = (-1, 1)");

		clickRW(-1, 1);
		clickRW(-1, 1);

		checkContent("A = (-1, 1)");
	}

	@Test
	void pointOnPathShouldBeUsedAsVectorStart() {
		GeoLine path = add("g: y = -1");

		clickRW(2, -1);
		clickRW(-1, 1);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertTrue(startPoint.isPointOnPath());

		path.setCoords(0, 1, 2);
		path.updateCascade();

		checkContent("g: y = -2", "A = (2, -2)", "B = (-1, 1)", "u = (-3, 3)");
		assertVector("u", startPoint, -3, 3);
	}

	@Test
	void intersectionPointShouldBeUsedAsVectorStart() {
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

		checkContent("g: y = -2", "h: x = 3", "A = (3, -2)", "B = (-1, 1)", "u = (-4, 3)");
		assertVector("u", intersection, -4, 3);
	}

	@Test
	void clickingPolygonFillShouldCreateFreeStartPoint() {
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
		GeoVector vector = getOnlyNewObject(existingObjects, GeoVector.class);
		assertVector(vector, points.get(0), 3, -3);
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
		GeoVector vector = assertInstanceOf(GeoVector.class, lookup("u"));
		assertVector(
				vector,
				startPoint,
				endpoint.getInhomX() - startPoint.getInhomX(),
				endpoint.getInhomY() - startPoint.getInhomY());
		assertEquals(-Math.PI / 6, Math.atan2(vector.getY(), vector.getX()), 1E-8);
		assertEquals(Math.sqrt(5), Math.hypot(vector.getX(), vector.getY()), 1E-8);
	}

	@Test
	void draggingBetweenEmptyLocationsShouldCreatePointsAndVector() {
		dragRW(-1, 1, 2, -1);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		checkContent("A = (-1, 1)", "B = (2, -1)", "u = (3, -2)");
		assertVector("u", startPoint, 3, -2);
	}

	@Test
	void draggingFromExistingPointShouldCreateSecondPointAndVector() {
		GeoPoint startPoint = add("A = (-1, 1)");

		dragRW(-1, 1, 2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "u = (3, -2)");
		assertVector("u", startPoint, 3, -2);
	}

	@Test
	void draggingToExistingPointShouldCreateFirstPointAndVector() {
		add("A = (2, -1)");

		dragRW(-1, 1, 2, -1);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("B"));
		checkContent("A = (2, -1)", "B = (-1, 1)", "u = (3, -2)");
		assertVector("u", startPoint, 3, -2);
	}

	@Test
	void draggingBetweenExistingPointsShouldCreateVector() {
		GeoPoint startPoint = add("A = (-1, 1)");
		add("B = (2, -1)");

		dragRW(-1, 1, 2, -1);

		checkContent("A = (-1, 1)", "B = (2, -1)", "u = (3, -2)");
		assertVector("u", startPoint, 3, -2);
	}

	@Test
	void releasingNewSecondPointAwayFromPressShouldUseReleasePosition() {
		clickRW(0, 0);
		pressRW(2, -2);
		releaseRW(4, -3);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		checkContent("A = (0, 0)", "B = (4, -3)", "u = (4, -3)");
		assertVector("u", startPoint, 4, -3);
	}

	@Test
	void releasingExistingSecondPointAwayFromPressShouldMoveIt() {
		add("A = (2, -2)");
		clickRW(0, 0);

		pressRW(2, -2);
		releaseRW(4, -3);

		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("B"));
		checkContent("A = (4, -3)", "B = (0, 0)", "u = (4, -3)");
		assertVector("u", startPoint, 4, -3);
	}

	@Test
	void toolShouldCreateAnotherVectorAfterCompletion() {
		dragRW(-1, 1, 2, -1);
		dragRW(-1, 1, 3, -2);

		checkContent("A = (-1, 1)", "B = (2, -1)", "u = (3, -2)", "C = (3, -2)", "v = (4, -3)");
		GeoPoint startPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		assertVector("v", startPoint, 4, -3);
	}

	private void assertVector(
			String label, GeoPoint expectedStart, double expectedX, double expectedY) {
		GeoVector vector = assertInstanceOf(GeoVector.class, lookup(label));
		assertVector(vector, expectedStart, expectedX, expectedY);
	}

	private void assertVector(
			GeoVector vector, GeoPoint expectedStart, double expectedX, double expectedY) {
		assertAll(
				() -> assertSame(expectedStart, vector.getStartPoint()),
				() -> assertEquals(expectedX, vector.getX(), 1E-8),
				() -> assertEquals(expectedY, vector.getY(), 1E-8));
	}
}
