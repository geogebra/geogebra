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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_COMPASSES;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CompassToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_COMPASSES);
	}

	@Test
	void twoExistingPointsDefineRadius() {
		add("A = (2, -2)");
		add("B = (0, 0)");
		clickRW(2, -2);
		clickRW(0, 0);
		clickRW(3, 0);
		checkContent("A = (2, -2)", "B = (0, 0)", "C = (3, 0)", unicode("c: (x - 3)^2 + y^2 = 8"));
	}

	@Test
	@Issue("APPS-6270")
	void onTheFlyPointsDefineRadius() {
		clickRW(2, -2);
		clickRW(0, 0);
		clickRW(3, 0);
		checkContent("A = (2, -2)", "B = (0, 0)", "C = (3, 0)", unicode("c: (x - 3)^2 + y^2 = 8"));
	}

	@Test
	void existingSegmentDefinesRadius() {
		add("A = (2, -2)");
		add("B = (0, 0)");
		add("Segment(A,B)");
		clickRW(1, -1);
		clickRW(3, 0);
		checkContent(
				"A = (2, -2)",
				"B = (0, 0)",
				"f = 2.82843",
				"C = (3, 0)",
				unicode("c: (x - 3)^2 + y^2 = 8"));
	}

	@Test
	void existingCircleDefinesRadius() {
		add("A = (2, -2)");
		add("B = (0, 0)");
		add("Circle(A,B)");
		clickRW(4, -4);
		clickRW(3, 0);
		checkContent(
				"A = (2, -2)",
				"B = (0, 0)",
				unicode("c: (x - 2)^2 + (y + 2)^2 = 8"),
				"C = (3, 0)",
				unicode("d: (x - 3)^2 + y^2 = 8"));
	}

	@Test
	void rejectsEllipseThenAcceptsSegmentAndExistingCenter() {
		add("e: (x - 4)^2 / 9 + (y + 4)^2 = 1");
		add("A = (1, -1)");
		add("B = (3, -1)");
		add("s = Segment(A, B)");
		add("C = (8, -2)");
		Set<String> before = getObjectNames();
		moveMouseRW(4, -3);
		clickRW(4, -3);
		assertEquals(before, getObjectNames());
		clickRW(2, -1);
		moveMouseRW(8, -2);
		assertTrue(ec.getHighlightedgeos().contains(lookup("C")));
		assertEquals(before, getObjectNames());
		clickRW(8, -2);
		GeoConic circle = getOnlyNewObject(before, GeoConic.class);
		assertCircleAt(circle, 8, -2, 2);
		add("SetCoords(B, 4, -1)");
		assertCircleAt(circle, 8, -2, 3);
		add("SetCoords(C, 8, -3)");
		assertCircleAt(circle, 8, -3, 3);
	}

	@Test
	void copiedCircleRadiusRemainsDependent() {
		add("A = (1, -1)");
		add("B = (4, -1)");
		add("c = Circle(A, B)");
		add("C = (8, -2)");
		clickRW(1, -4);
		moveMouseRW(8, -2);
		assertTrue(ec.getHighlightedgeos().contains(lookup("C")));
		clickRW(8, -2);
		GeoConic circle = (GeoConic) lookup("d");
		assertCircleAt(circle, 8, -2, 3);
		add("SetCoords(B, 5, -1)");
		assertCircleAt(circle, 8, -2, 4);
		add("SetCoords(C, 8, -3)");
		assertCircleAt(circle, 8, -3, 4);
	}

	@Test
	void distanceRadiusHighlightsExistingCenterAndUpdates() {
		add("A = (1, -1)");
		add("B = (4, -1)");
		add("C = (8, -2)");
		add("s = Segment(A, B)");
		Set<String> before = getObjectNames();
		clickRW(1, -1);
		clickRW(4, -1);
		assertEquals(before, getObjectNames(), "Endpoints must take precedence over segment");
		moveMouseRW(8, -2);
		assertTrue(ec.getHighlightedgeos().contains(lookup("C")));
		clickRW(8, -2);
		GeoConic circle = (GeoConic) lookup("c");
		assertCircleAt(circle, 8, -2, 3);
		add("SetCoords(B, 5, -1)");
		assertCircleAt(circle, 8, -2, 4);
		add("SetCoords(C, 8, -3)");
		assertCircleAt(circle, 8, -3, 4);
	}

	@ParameterizedTest
	@ValueSource(strings = {"points", "segment", "circle"})
	void consecutiveConstructionsClearRadiusAndCenterSelections(String radiusSource) {
		add("A = (1, -1)");
		add("B = (4, -1)");
		add("C = (8, -2)");
		add("D = (12, -2)");
		if (radiusSource.equals("segment")) {
			add("s = Segment(A, B)");
		} else if (radiusSource.equals("circle")) {
			add("s = Circle(A, B)");
		}
		for (int construction = 0; construction < 2; construction++) {
			Set<String> before = getObjectNames();
			if (radiusSource.equals("points")) {
				clickRW(1, -1);
				clickRW(4, -1);
			} else if (radiusSource.equals("segment")) {
				clickRW(2, -1);
			} else {
				clickRW(1, -4);
			}
			assertEquals(before, getObjectNames(), "Radius selection must not create output");
			double centerX = 8 + 4 * construction;
			moveMouseRW(centerX, -2);
			assertEquals(before, getObjectNames());
			clickRW(centerX, -2);
			GeoConic circle = getOnlyNewObject(before, GeoConic.class);
			assertEquals(before.size() + 1, getObjectNames().size());
			assertCircleAt(circle, centerX, -2, 3);
		}
	}

	private void assertCircleAt(GeoConic circle, double x, double y, double radius) {
		assertTrue(circle.isDefined());
		assertEquals(Commands.Circle, circle.getParentAlgorithm().getClassName());
		assertEquals(x, circle.getMidpoint2D().getX(), 1e-8);
		assertEquals(y, circle.getMidpoint2D().getY(), 1e-8);
		assertEquals(radius, circle.getHalfAxis(0), 1e-8);
	}
}
