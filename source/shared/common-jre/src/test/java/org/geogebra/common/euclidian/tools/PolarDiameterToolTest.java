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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_POLAR_DIAMETER;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoVector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PolarDiameterToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_POLAR_DIAMETER);
		EuclidianView view = getApp().getActiveEuclidianView();
		view.setCoordSystem(250, 250, 50, 50);
	}

	@Test
	void pointThenConicShouldCreatePolarLine() {
		GeoPoint point = add("A = (4, 2)");
		addEllipse();
		Set<String> existingObjects = getObjectNames();

		clickRW(4, 2);
		clickRW(0, 1);

		GeoLine polar = getOnlyNewObject(existingObjects, GeoLine.class);
		assertLineThrough(polar, 1, 0, -1, 1);

		point.setCoords(8, 0, 1);
		point.updateCascade();

		assertLineThrough(polar, 0.5, 0, 0.5, 1);
	}

	@Test
	void conicThenPointShouldCreatePolarLine() {
		add("A = (4, 2)");
		addEllipse();
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 1);
		clickRW(4, 2);

		assertLineThrough(getOnlyNewObject(existingObjects, GeoLine.class), 1, 0, -1, 1);
	}

	@Test
	void lineThenConicShouldCreateConjugateDiameter() {
		GeoLine direction = add("g: y = x + 3");
		addEllipse();
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 3);
		clickRW(0, 1);

		GeoLine diameter = getOnlyNewObject(existingObjects, GeoLine.class);
		assertLineThrough(diameter, 0, 0, 4, -1);

		direction.setCoords(1, 0, -3);
		direction.updateCascade();

		assertLineThrough(diameter, 0, 0, 1, 0);
	}

	@Test
	void conicThenLineShouldCreateConjugateDiameter() {
		add("g: y = x + 3");
		addEllipse();
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 1);
		clickRW(0, 3);

		assertLineThrough(getOnlyNewObject(existingObjects, GeoLine.class), 0, 0, 4, -1);
	}

	@Test
	void vectorThenConicShouldCreateConjugateDiameter() {
		add("A = (-4, -3)");
		GeoPoint directionEnd = add("B = (-2, -1)");
		add("u = Vector(A, B)");
		addEllipse();
		Set<String> existingObjects = getObjectNames();

		clickRW(-3, -2);
		clickRW(0, 1);

		GeoLine diameter = getOnlyNewObject(existingObjects, GeoLine.class);
		assertLineThrough(diameter, 0, 0, 4, -1);

		directionEnd.setCoords(-4, -1, 1);
		directionEnd.updateCascade();

		assertLineThrough(diameter, 0, 0, 1, 0);
	}

	@Test
	void conicThenVectorShouldCreateConjugateDiameter() {
		add("u = Vector((-4, -3), (-2, -1))");
		addEllipse();
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 1);
		clickRW(-3, -2);

		assertLineThrough(getOnlyNewObject(existingObjects, GeoLine.class), 0, 0, 4, -1);
	}

	@Test
	void emptyClickShouldBeIgnoredWithoutClearingSelectedConic() {
		add("A = (4, 2)");
		addEllipse();
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 1);
		clickRW(4, -4);
		clickRW(4, 2);

		assertLineThrough(getOnlyNewObject(existingObjects, GeoLine.class), 1, 0, -1, 1);
	}

	@Test
	void linearFunctionShouldBeIgnoredWithoutClearingSelectedConic() {
		add("A = (4, 2)");
		add("q(x) = x + 3");
		addEllipse();
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 1);
		clickRW(0, 3);
		clickRW(4, 2);

		assertLineThrough(getOnlyNewObject(existingObjects, GeoLine.class), 1, 0, -1, 1);
	}

	@Test
	void linearFunctionShouldBeIgnoredWhileLineIsSelected() {
		add("g: y = x + 3");
		add("q(x) = -x - 4");
		addEllipse();
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 3);
		clickRW(0, -4);
		clickRW(0, 1);

		assertLineThrough(getOnlyNewObject(existingObjects, GeoLine.class), 0, 0, 4, -1);
	}

	@Test
	void linearFunctionShouldBeIgnoredWhileVectorIsSelected() {
		add("u = Vector((-4, -3), (-2, -1))");
		add("q(x) = -x - 4");
		addEllipse();
		Set<String> existingObjects = getObjectNames();

		clickRW(-3, -2);
		clickRW(0, -4);
		clickRW(0, 1);

		assertLineThrough(getOnlyNewObject(existingObjects, GeoLine.class), 0, 0, 4, -1);
	}

	@Test
	void secondConicShouldBeIgnoredWhileFirstConicIsSelected() {
		add("A = (4, 4)");
		GeoConic firstConic = addEllipse();
		GeoConic secondConic = add("d: (x - 6)^2 + y^2 = 4");
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 1);
		clickRW(6, 2);
		assertAll(
				() -> assertTrue(getApp().getSelectionManager().containsSelectedGeo(firstConic)),
				() -> assertFalse(getApp().getSelectionManager().containsSelectedGeo(secondConic)));
		clickRW(4, 4);

		assertLineThrough(getOnlyNewObject(existingObjects, GeoLine.class), 1, 0, -3, 1);
	}

	@Test
	void pointShouldHaveHitPriorityOverConicAtSameLocation() {
		add("A = (0, 1)");
		addEllipse();
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 1);
		clickRW(2, 0);

		assertLineThrough(getOnlyNewObject(existingObjects, GeoLine.class), 0, 1, 1, 1);
	}

	@Test
	void hoveringSupportedObjectsShouldHighlightWithoutCreatingLine() {
		GeoPoint point = add("A = (4, 2)");
		GeoLine line = add("g: y = x + 4");
		GeoVector vector = add("u = Vector((-4, -3), (-2, -1))");
		GeoConic conic = addEllipse();
		final Set<String> existingObjects = getObjectNames();

		moveMouseRW(4, 2);
		assertTrue(ec.getHighlightedgeos().contains(point));
		moveMouseRW(0, 4);
		assertTrue(ec.getHighlightedgeos().contains(line));
		moveMouseRW(-3, -2);
		assertTrue(ec.getHighlightedgeos().contains(vector));
		moveMouseRW(0, 1);

		assertAll(
				() -> assertTrue(ec.getHighlightedgeos().contains(conic)),
				() -> assertEquals(existingObjects, getObjectNames()));
	}

	private GeoConic addEllipse() {
		return add("c: x^2 + 4y^2 = 4");
	}

	private void assertLineThrough(
			GeoLine line, double firstX, double firstY, double secondX, double secondY) {
		double norm = Math.hypot(line.getX(), line.getY());
		assertAll(
				() -> assertTrue(line.isDefined()),
				() -> assertEquals(
						0, (line.getX() * firstX + line.getY() * firstY + line.getZ()) / norm, 1E-8),
				() -> assertEquals(
						0, (line.getX() * secondX + line.getY() * secondY + line.getZ()) / norm, 1E-8));
	}
}
