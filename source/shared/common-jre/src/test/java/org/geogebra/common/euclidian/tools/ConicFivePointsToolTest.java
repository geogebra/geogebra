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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CONIC_FIVE_POINTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConicFivePointsToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_CONIC_FIVE_POINTS);
	}

	@Test
	void fiveClicksCreateConic() {
		Set<String> existingObjects = getObjectNames();

		clickCirclePoints(3);

		GeoConic conic = getOnlyNewObject(existingObjects, GeoConic.class);
		assertTrue(conic.isCircle());
		assertContainsPoints(conic, "A", "B", "C", "D", "E");
	}

	@Test
	void clickingExistingPointsCreatesConic() {
		add("A = (5, -3)");
		add("B = (3, -1)");
		add("C = (1, -3)");
		add("D = (3, -5)");
		add("E = (4.2, -1.4)");
		Set<String> existingObjects = getObjectNames();

		clickCirclePoints(3);

		GeoConic conic = getOnlyNewObject(existingObjects, GeoConic.class);
		assertTrue(conic.isCircle());
		assertContainsPoints(conic, "A", "B", "C", "D", "E");
	}

	@Test
	void mixedNewAndExistingPointsShouldCreateConic() {
		add("B = (3, -1)");
		add("D = (3, -5)");
		Set<String> existingObjects = getObjectNames();

		clickCirclePoints(3);

		GeoConic conic = getOnlyNewObject(existingObjects, GeoConic.class);
		assertTrue(conic.isCircle());
		assertContainsPoints(conic, "A", "B", "C", "D", "E");
	}

	@Test
	void clickingOnLineCreatesPointOnLine() {
		add("f: y = -3");
		Set<String> existingObjects = getObjectNames();

		clickCirclePoints(3);

		assertEquals("Point(f)", lookup("A").getDefinition(StringTemplate.testTemplate));
		GeoConic conic = getOnlyNewObject(existingObjects, GeoConic.class);
		assertTrue(conic.isCircle());
	}

	@Test
	void clickingAtAxesIntersectionCreatesIntersectionPoint() {
		getApp().getActiveEuclidianView().setShowAxes(true, true);
		clickRW(0, 0);
		clickRW(4, 0);
		clickRW(0, -4);
		clickRW(4, -4);
		clickRW(2, -4.82842712474619);
		assertEquals("Intersect(xAxis, yAxis)", lookup("A").getDefinition(StringTemplate.testTemplate));
		GeoConic conic = (GeoConic) lookup("c");
		assertTrue(conic.isDefined());
	}

	@Test
	void collinearPointsMakeConicUndefined() {
		clickRW(0, 0);
		clickRW(1, 0);
		clickRW(2, 0);
		clickRW(3, 0);
		clickRW(4, 0);
		GeoConic conic = (GeoConic) lookup("c");
		assertFalse(conic.isDefined());
	}

	@Test
	void secondConstructionShouldStartWithEmptySelection() {
		clickCirclePoints(3);
		Set<String> existingObjects = getObjectNames();

		clickCirclePoints(8);

		GeoConic secondConic = getOnlyNewObject(existingObjects, GeoConic.class);
		assertTrue(secondConic.isCircle());
	}

	private void clickCirclePoints(double centerX) {
		clickRW(centerX + 2, -3);
		clickRW(centerX, -1);
		clickRW(centerX - 2, -3);
		clickRW(centerX, -5);
		clickRW(centerX + 1.2, -1.4);
	}

	private void assertContainsPoints(GeoConic conic, String... pointLabels) {
		for (String pointLabel : pointLabels) {
			assertTrue(conic.isOnPath((GeoPointND) lookup(pointLabel), 1e-8));
		}
	}
}
