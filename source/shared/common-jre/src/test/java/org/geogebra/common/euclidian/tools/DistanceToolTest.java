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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_DISTANCE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoSegment;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DistanceToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_DISTANCE);
	}

	@Test
	void twoPointsShouldCreateDistanceText() {
		GeoPoint firstPoint = add("A = (1, -1)");
		GeoPoint secondPoint = add("B = (4, -5)");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -1);
		clickRW(4, -5);

		assertDistanceText(existingObjects, 5, firstPoint, secondPoint);
	}

	@Test
	void pointThenLineShouldCreateDistanceText() {
		GeoPoint point = add("A = (1, -1)");
		GeoLine line = add("g: y = -3");
		Set<String> existingObjects = getObjectNames();

		clickRW(1, -1);
		clickRW(4, -3);

		assertDistanceText(existingObjects, 2, point, line);
	}

	@Test
	void lineThenPointShouldCreateDistanceText() {
		GeoPoint point = add("A = (1, -1)");
		GeoLine line = add("g: y = -3");
		Set<String> existingObjects = getObjectNames();

		clickRW(4, -3);
		clickRW(1, -1);

		assertDistanceText(existingObjects, 2, point, line);
	}

	@Test
	void pointThenSegmentShouldMeasurePointToSegment() {
		GeoPoint point = add("A = (5, -1)");
		add("B = (1, -3)");
		add("C = (3, -3)");
		GeoSegment segment = add("f = Segment(B, C)");
		Set<String> existingObjects = getObjectNames();

		clickRW(5, -1);
		clickRW(2, -3);

		assertAll(
				() -> assertDistanceText(existingObjects, Math.sqrt(8), point, segment),
				() -> assertTrue(getApp().getSelectionManager().getSelectedSegmentList().isEmpty()));
	}

	@Test
	void hiddenSegmentLabelShouldShowItsValue() {
		add("A = (1, -2)");
		add("B = (4, -2)");
		GeoSegment segment = add("f = Segment(A, B)");
		segment.setLabelVisible(false);
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		assertAll(
				() -> assertEquals(existingObjects, getObjectNames()),
				() -> assertTrue(segment.isLabelVisible()),
				() -> assertEquals(GeoElementND.LABEL_VALUE, segment.getLabelMode()));
	}

	@Test
	void visibleSegmentLabelShouldShowNameAndValue() {
		add("A = (1, -2)");
		add("B = (4, -2)");
		GeoSegment segment = add("f = Segment(A, B)");
		segment.setLabelVisible(true);
		segment.setLabelMode(GeoElementND.LABEL_NAME);
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		assertAll(
				() -> assertEquals(existingObjects, getObjectNames()),
				() -> assertTrue(segment.isLabelVisible()),
				() -> assertEquals(GeoElementND.LABEL_NAME_VALUE, segment.getLabelMode()));
	}

	@Test
	void twoParallelLinesShouldCreateDistance() {
		GeoLine firstLine = add("g: y = -1");
		GeoLine secondLine = add("h: y = -3");
		Set<String> existingObjects = getObjectNames();

		clickRW(4, -1);
		clickRW(4, -3);

		GeoNumeric distance = getOnlyNewObject(existingObjects, GeoNumeric.class);
		assertAll(
				() -> assertEquals(2, distance.getDouble(), 1E-8),
				() -> assertTrue(firstLine.isParentOf(distance)),
				() -> assertTrue(secondLine.isParentOf(distance)));
	}

	@Test
	void circleShouldCreateCircumferenceText() {
		GeoElement circle = add("c = Circle((2, -2), 1)");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -2);

		assertMeasurementText(existingObjects, 2 * Math.PI, circle);
	}

	@Test
	void arcShouldCreateArcLengthText() {
		GeoElement arc = add("a = CircularArc((2, -2), (3, -2), (2, -1))");
		Set<String> existingObjects = getObjectNames();

		clickRW(2.7, -1.3);

		assertMeasurementText(existingObjects, Math.PI / 2, arc);
	}

	@Test
	void polygonShouldCreatePerimeterText() {
		add("A = (1, -1)");
		add("B = (5, -1)");
		add("C = (5, -3)");
		add("D = (1, -3)");
		GeoElement polygon = add("p = Polygon(A, B, C, D)");
		Set<String> existingObjects = getObjectNames();

		clickRW(3, -2);

		assertMeasurementText(existingObjects, 12, polygon);
	}

	@Test
	void polylineShouldCreatePerimeterText() {
		add("A = (1, -1)");
		add("B = (4, -1)");
		add("C = (4, -5)");
		GeoElement polyline = add("p = Polyline(A, B, C)");
		Set<String> existingObjects = getObjectNames();

		clickRW(4, -3);

		GeoText text = getOnlyNewObject(existingObjects, GeoText.class);
		assertAll(
				() -> assertTrue(text.isEuclidianVisible()),
				() -> assertTextContainsValue(text, polyline),
				() -> assertTrue(polyline.isParentOf(text)));
	}

	@Test
	void clickingEmptyViewShouldDoNothing() {
		Set<String> existingObjects = getObjectNames();

		clickRW(2, -2);

		assertEquals(existingObjects, getObjectNames());
	}

	@Test
	void lineThenSegmentShouldShowSegmentLength() {
		GeoLine line = add("g: y = -1");
		add("A = (1, -3)");
		add("B = (4, -3)");
		GeoSegment segment = add("f = Segment(A, B)");
		segment.setLabelVisible(false);
		Set<String> existingObjects = getObjectNames();

		clickRW(5, -1);
		assertTrue(getApp().getSelectionManager().containsSelectedGeo(line));
		clickRW(2, -3);

		assertAll(
				() -> assertEquals(existingObjects, getObjectNames()),
				() -> assertTrue(segment.isLabelVisible()),
				() -> assertEquals(GeoElementND.LABEL_VALUE, segment.getLabelMode()));
	}

	private void assertDistanceText(
			Set<String> existingObjects, double expectedDistance, GeoElement... inputs) {
		GeoNumeric distance = getOnlyNewObject(existingObjects, GeoNumeric.class);
		GeoText text = getOnlyNewObject(existingObjects, GeoText.class);
		assertAll(
				() -> assertEquals(expectedDistance, distance.getDouble(), 1E-8),
				() -> assertTrue(text.isEuclidianVisible()),
				() -> assertTextContainsValue(text, distance),
				() -> assertTrue(Arrays.stream(inputs).allMatch(input -> input.isParentOf(distance))),
				() -> assertTrue(Arrays.stream(inputs).allMatch(input -> input.isParentOf(text))));
	}

	private void assertMeasurementText(
			Set<String> existingObjects, double expectedValue, GeoElement input) {
		GeoNumeric measurement = getOnlyNewObject(existingObjects, GeoNumeric.class);
		GeoText text = getOnlyNewObject(existingObjects, GeoText.class);
		assertAll(
				() -> assertEquals(expectedValue, measurement.getDouble(), 1E-8),
				() -> assertTrue(text.isEuclidianVisible()),
				() -> assertTextContainsValue(text, measurement),
				() -> assertTrue(input.isParentOf(measurement)),
				() -> assertTrue(input.isParentOf(text)));
	}
}
