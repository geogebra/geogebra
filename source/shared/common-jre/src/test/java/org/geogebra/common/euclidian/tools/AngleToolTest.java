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

import static org.geogebra.common.BaseUnitTest.hasValue;
import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ANGLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.kernel.geos.GeoAngle;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AngleToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_ANGLE);
	}

	@Test
	void threeExistingPointsShouldCreateAngle() {
		GeoPoint firstPoint = add("A = (4, -3)");
		GeoPoint vertex = add("B = (2, -3)");
		GeoPoint thirdPoint = add("C = (2, -1)");
		int objectCount = getObjectCount();

		clickRW(4, -3);
		clickRW(2, -3);
		clickRW(2, -1);

		GeoAngle angle = getAngles(objectCount + 1, 1).get(0);
		assertAll(
				() -> assertEquals(Math.PI / 2, angle.getDouble(), 1E-8),
				() -> assertTrue(firstPoint.isParentOf(angle)),
				() -> assertTrue(vertex.isParentOf(angle)),
				() -> assertTrue(thirdPoint.isParentOf(angle)));
	}

	@Test
	void threeEmptyClicksShouldCreatePointsAndAngle() {
		clickRW(4, -3);
		clickRW(2, -3);
		clickRW(2, -1);

		GeoAngle angle = getAngles(4, 1).get(0);
		GeoPoint firstPoint = assertInstanceOf(GeoPoint.class, lookup("A"));
		GeoPoint vertex = assertInstanceOf(GeoPoint.class, lookup("B"));
		GeoPoint thirdPoint = assertInstanceOf(GeoPoint.class, lookup("C"));
		assertAll(
				() -> assertThat(firstPoint, hasValue("(4, -3)")),
				() -> assertThat(vertex, hasValue("(2, -3)")),
				() -> assertThat(thirdPoint, hasValue("(2, -1)")),
				() -> assertEquals(Math.PI / 2, angle.getDouble(), 1E-8),
				() -> assertTrue(firstPoint.isParentOf(angle)),
				() -> assertTrue(vertex.isParentOf(angle)),
				() -> assertTrue(thirdPoint.isParentOf(angle)));
	}

	@Test
	void twoVectorsShouldCreateAngle() {
		GeoVector firstVector = add("u = Vector((1, -2), (4, -2))");
		GeoVector secondVector = add("v = Vector((2, -4), (2, -1))");
		int objectCount = getObjectCount();

		clickRW(3, -2);
		clickRW(2, -3);

		GeoAngle angle = getAngles(objectCount + 1, 1).get(0);
		assertAll(
				() -> assertEquals(Math.PI / 2, angle.getDouble(), 1E-8),
				() -> assertTrue(firstVector.isParentOf(angle)),
				() -> assertTrue(secondVector.isParentOf(angle)));
	}

	@Test
	void twoLinesShouldCreateAngle() {
		GeoLine firstLine = add("g: x = 2");
		GeoLine secondLine = add("h: y = -2");
		int objectCount = getObjectCount();

		clickRW(2, -4);
		clickRW(4, -2);

		GeoAngle angle = getAngles(objectCount + 1, 1).get(0);
		assertAll(
				() -> assertEquals(Math.PI / 2, angle.getDouble(), 1E-8),
				() -> assertTrue(firstLine.isParentOf(angle)),
				() -> assertTrue(secondLine.isParentOf(angle)));
	}

	@Test
	void twoConnectedSegmentsShouldCreateAngleAtCommonEndpoint() {
		GeoPoint firstPoint = add("A = (1, -1)");
		GeoPoint vertex = add("B = (3, -2)");
		GeoPoint thirdPoint = add("C = (3, -4)");
		add("f = Segment(A, B)");
		add("g = Segment(B, C)");
		int objectCount = getObjectCount();

		clickRW(2, -1.5);
		clickRW(3, -3);

		GeoAngle angle = getAngles(objectCount + 1, 1).get(0);
		assertAll(
				() -> assertEquals(Math.acos(-1 / Math.sqrt(5)), angle.getDouble(), 1E-8),
				() -> assertTrue(firstPoint.isParentOf(angle)),
				() -> assertTrue(vertex.isParentOf(angle)),
				() -> assertTrue(thirdPoint.isParentOf(angle)));
	}

	@Test
	void twoDisjointSegmentsShouldCreateAngleBetweenSupportingLines() {
		add("A = (1, -1)");
		add("B = (4, -1)");
		add("C = (2, -4)");
		add("D = (2, -2)");
		GeoSegment firstSegment = add("f = Segment(A, B)");
		GeoSegment secondSegment = add("g = Segment(C, D)");
		int objectCount = getObjectCount();

		clickRW(3, -1);
		clickRW(2, -3);

		GeoAngle angle = getAngles(objectCount + 1, 1).get(0);
		assertAll(
				() -> assertEquals(Math.PI / 2, angle.getDouble(), 1E-8),
				() -> assertTrue(firstSegment.isParentOf(angle)),
				() -> assertTrue(secondSegment.isParentOf(angle)));
	}

	@Test
	void clickingConcavePolygonShouldCreateAllInteriorAngles() {
		add("A = (1, -1)");
		add("B = (5, -1)");
		add("C = (3, -2)");
		add("D = (5, -4)");
		add("E = (1, -4)");
		GeoPolygon polygon = add("p = Polygon(A, B, C, D, E)");
		int objectCount = getObjectCount();

		clickRW(2, -3);

		List<GeoAngle> angles = getAngles(objectCount + 5, 5);
		assertAll(
				() -> assertTrue(angles.stream().allMatch(polygon::isParentOf)),
				() -> assertTrue(angles.stream().anyMatch(angle -> angle.getDouble() > Math.PI)),
				() -> assertEquals(
						3 * Math.PI, angles.stream().mapToDouble(GeoAngle::getDouble).sum(), 1E-8));
	}

	@Test
	void lineThenVectorShouldIgnoreVectorAndUseSecondLine() {
		GeoLine firstLine = add("g: y = -1");
		GeoVector ignoredVector = add("u = Vector((1, -3), (4, -3))");
		GeoLine secondLine = add("h: x = 4");
		int objectCount = getObjectCount();

		clickRW(5, -1);
		clickRW(2, -3);
		clickRW(4, -4);

		GeoAngle angle = getAngles(objectCount + 1, 1).get(0);
		assertAll(
				() -> assertEquals(3 * Math.PI / 2, angle.getDouble(), 1E-8),
				() -> assertTrue(firstLine.isParentOf(angle)),
				() -> assertTrue(secondLine.isParentOf(angle)),
				() -> assertFalse(ignoredVector.isParentOf(angle)));
	}

	@Test
	void vectorThenLineShouldIgnoreLineAndUseSecondVector() {
		GeoVector firstVector = add("u = Vector((1, -2), (4, -2))");
		GeoLine ignoredLine = add("g: y = -4");
		GeoVector secondVector = add("v = Vector((2, -4), (2, -1))");
		int objectCount = getObjectCount();

		clickRW(3, -2);
		clickRW(5, -4);
		clickRW(2, -3);

		GeoAngle angle = getAngles(objectCount + 1, 1).get(0);
		assertAll(
				() -> assertEquals(Math.PI / 2, angle.getDouble(), 1E-8),
				() -> assertTrue(firstVector.isParentOf(angle)),
				() -> assertTrue(secondVector.isParentOf(angle)),
				() -> assertFalse(ignoredLine.isParentOf(angle)));
	}

	@Test
	void pointThenLineShouldIgnoreLineAndUseTwoMorePoints() {
		GeoPoint firstPoint = add("A = (4, -3)");
		GeoPoint vertex = add("B = (2, -3)");
		GeoPoint thirdPoint = add("C = (2, -1)");
		GeoLine ignoredLine = add("g: y = -5");
		int objectCount = getObjectCount();

		clickRW(4, -3);
		clickRW(5, -5);
		clickRW(2, -3);
		clickRW(2, -1);

		GeoAngle angle = getAngles(objectCount + 1, 1).get(0);
		assertAll(
				() -> assertEquals(Math.PI / 2, angle.getDouble(), 1E-8),
				() -> assertTrue(firstPoint.isParentOf(angle)),
				() -> assertTrue(vertex.isParentOf(angle)),
				() -> assertTrue(thirdPoint.isParentOf(angle)),
				() -> assertFalse(ignoredLine.isParentOf(angle)));
	}

	@Test
	void threePointsShouldTakePrecedenceOverSelectedLine() {
		GeoLine ignoredLine = add("g: y = -5");
		GeoPoint firstPoint = add("A = (4, -3)");
		GeoPoint vertex = add("B = (2, -3)");
		GeoPoint thirdPoint = add("C = (2, -1)");
		int objectCount = getObjectCount();

		clickRW(5, -5);
		clickRW(4, -3);
		clickRW(2, -3);
		clickRW(2, -1);

		GeoAngle angle = getAngles(objectCount + 1, 1).get(0);
		assertAll(
				() -> assertEquals(Math.PI / 2, angle.getDouble(), 1E-8),
				() -> assertTrue(firstPoint.isParentOf(angle)),
				() -> assertTrue(vertex.isParentOf(angle)),
				() -> assertTrue(thirdPoint.isParentOf(angle)),
				() -> assertFalse(ignoredLine.isParentOf(angle)));
	}

	private List<GeoAngle> getAngles(int expectedObjectCount, int expectedAngleCount) {
		String[] objectNames = getApp().getGgbApi().getAllObjectNames();
		assertEquals(expectedObjectCount, objectNames.length);
		List<GeoAngle> angles = new ArrayList<>();
		for (String label : objectNames) {
			GeoElement geo = lookup(label);
			if (geo instanceof GeoAngle angle) {
				angles.add(angle);
			}
		}
		assertEquals(expectedAngleCount, angles.size());
		assertTrue(angles.stream().allMatch(angle -> angle.isDefined() && angle.isEuclidianVisible()));
		return angles;
	}

	private int getObjectCount() {
		return getApp().getGgbApi().getAllObjectNames().length;
	}
}
