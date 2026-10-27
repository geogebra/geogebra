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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_VECTOR_FROM_POINT;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VectorFromPointToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_VECTOR_FROM_POINT);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@Test
	void pointThenVectorShouldCopyVectorToPoint() {
		GeoPoint startPoint = add("A = (-2, -2)");
		add("u = Vector((0, 0), (4, 2))");

		clickRW(-2, -2);
		clickRW(2, 1);

		checkContent("A = (-2, -2)", "u = (4, 2)", "A' = (2, 0)", "v = (4, 2)");
		assertTranslatedPoint("A'", 2, 0);
		assertVector("v", startPoint, 4, 2);
	}

	@Test
	void vectorThenPointShouldCopyVectorToPoint() {
		GeoPoint startPoint = add("A = (-2, -2)");
		add("u = Vector((0, 0), (4, 2))");

		clickRW(2, 1);
		clickRW(-2, -2);

		checkContent("A = (-2, -2)", "u = (4, 2)", "A' = (2, 0)", "v = (4, 2)");
		assertTranslatedPoint("A'", 2, 0);
		assertVector("v", startPoint, 4, 2);
	}

	@Test
	void copiedVectorShouldFollowPointAndSourceVector() {
		GeoPoint startPoint = add("A = (-2, -2)");
		add("B = (0, 0)");
		GeoPoint sourceEnd = add("C = (4, 2)");
		add("u = Vector(B, C)");

		clickRW(-2, -2);
		clickRW(2, 1);

		GeoVector copiedVector = assertInstanceOf(GeoVector.class, lookup("v"));
		startPoint.setCoords(-3, 1, 1);
		startPoint.updateCascade();
		sourceEnd.setCoords(2, -4, 1);
		sourceEnd.updateCascade();

		assertAll(
				() -> assertTranslatedPoint("A'", -1, -3),
				() -> assertVector(copiedVector, startPoint, 2, -4));
	}

	@Test
	void emptyClickShouldBeIgnoredWithoutClearingSelectedPoint() {
		GeoPoint startPoint = add("A = (-2, -2)");
		add("u = Vector((0, 0), (4, 2))");

		clickRW(-2, -2);
		clickRW(4, -4);
		clickRW(2, 1);

		assertVector("v", startPoint, 4, 2);
	}

	@Test
	void incompatibleObjectShouldBeIgnoredWithoutClearingSelectedPoint() {
		GeoPoint startPoint = add("A = (-2, -2)");
		add("g: y = -4");
		add("u = Vector((0, 0), (4, 2))");

		clickRW(-2, -2);
		clickRW(1, -4);
		clickRW(2, 1);

		assertVector("v", startPoint, 4, 2);
	}

	@Test
	void secondPointShouldBeIgnoredWhileFirstPointIsSelected() {
		GeoPoint firstPoint = add("A = (-2, -2)");
		add("B = (-3, -3)");
		add("u = Vector((0, 0), (4, 2))");

		clickRW(-2, -2);
		clickRW(-3, -3);
		clickRW(2, 1);

		assertVector("v", firstPoint, 4, 2);
	}

	@Test
	void secondVectorShouldBeIgnoredWhileFirstVectorIsSelected() {
		GeoPoint startPoint = add("A = (-3, 2)");
		GeoVector firstVector = add("u = Vector((0, 0), (4, 0))");
		GeoVector secondVector = add("v = Vector((0, -2), (4, -2))");

		clickRW(2, 0);
		clickRW(2, -2);
		assertAll(
				() -> assertTrue(getApp().getSelectionManager().containsSelectedGeo(firstVector)),
				() -> assertFalse(getApp().getSelectionManager().containsSelectedGeo(secondVector)));
		clickRW(-3, 2);

		assertVector("w", startPoint, 4, 0);
	}

	@Test
	void clickingVectorEndpointShouldSelectPointBeforeVector() {
		GeoPoint vectorStart = add("A = (0, 0)");
		add("B = (4, 2)");
		add("u = Vector(A, B)");

		clickRW(0, 0);
		clickRW(2, 1);

		assertVector("v", vectorStart, 4, 2);
	}

	@Test
	void clickingSelectedPointAgainShouldDeselectIt() {
		add("A = (-2, -2)");
		GeoPoint secondPoint = add("B = (-3, -3)");
		add("u = Vector((0, 0), (4, 2))");

		clickRW(-2, -2);
		resetMouseLocation();
		clickRW(-2, -2);
		clickRW(2, 1);
		clickRW(-3, -3);

		assertVector("v", secondPoint, 4, 2);
	}

	@Test
	void toolShouldCreateAnotherVectorAfterCompletion() {
		GeoPoint firstPoint = add("A = (-2, -2)");
		GeoPoint secondPoint = add("B = (-3, -3)");
		add("u = Vector((0, 0), (4, 2))");

		clickRW(-2, -2);
		clickRW(2, 1);
		clickRW(-3, -3);
		clickRW(2, 1);

		assertAll(
				() -> assertVector("v", firstPoint, 4, 2), () -> assertVector("w", secondPoint, 4, 2));
	}

	@Test
	void hoveringPointShouldHighlightWithoutCreatingVector() {
		GeoPoint point = add("A = (-2, -2)");

		moveMouseRW(-2, -2);

		assertAll(
				() -> assertTrue(ec.getHighlightedgeos().contains(point)),
				() -> checkContent("A = (-2, -2)"));
	}

	@Test
	void hoveringVectorShouldHighlightWithoutCreatingVector() {
		GeoVector vector = add("u = Vector((0, 0), (4, 2))");

		moveMouseRW(2, 1);

		assertAll(
				() -> assertTrue(ec.getHighlightedgeos().contains(vector)),
				() -> checkContent("u = (4, 2)"));
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

	private void assertTranslatedPoint(String label, double expectedX, double expectedY) {
		GeoPoint translatedPoint = assertInstanceOf(GeoPoint.class, lookup(label));
		assertAll(
				() -> assertFalse(translatedPoint.isIndependent()),
				() -> assertEquals(expectedX, translatedPoint.getInhomX(), 1E-8),
				() -> assertEquals(expectedY, translatedPoint.getInhomY(), 1E-8));
	}
}
