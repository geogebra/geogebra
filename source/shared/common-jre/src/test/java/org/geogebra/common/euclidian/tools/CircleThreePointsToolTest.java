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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_THREE_POINTS;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CircleThreePointsToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_CIRCLE_THREE_POINTS);
	}

	@Test
	void threeClicksCreateCircle() {
		clickRW(0, 0);
		clickRW(2, -2);
		clickRW(2, 0);
		checkContent(
				"A = (0, 0)", "B = (2, -2)", "C = (2, 0)", unicode("c: (x - 1)^2 + (y + 1)^2 = 2"));
	}

	@Test
	void clickingExistingPointsCreatesCircle() {
		add("A = (0, 0)");
		add("B = (2, 0)");
		add("C = (0, -2)");
		clickRW(0, 0);
		clickRW(2, 0);
		clickRW(0, -2);
		checkContent(
				"A = (0, 0)", "B = (2, 0)", "C = (0, -2)", unicode("c: (x - 1)^2 + (y + 1)^2 = 2"));
	}

	@Test
	void collinearPointsCreateLineAndRecoverWhenPointMoves() {
		add("A = (1, -1)");
		add("B = (3, -1)");
		add("C = (5, -1)");
		clickRW(1, -1);
		clickRW(3, -1);
		clickRW(5, -1);
		GeoConic circle = (GeoConic) lookup("c");
		assertEquals(GeoConicNDConstants.CONIC_PARALLEL_LINES, circle.getType());
		add("SetCoords(C, 3, -3)");
		assertTrue(circle.isCircle());
		assertEquals(Math.sqrt(2), circle.getHalfAxis(0), 1e-8);
	}
}
