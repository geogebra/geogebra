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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_TWO_POINTS;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.kernel.geos.GeoConic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CircleToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_CIRCLE_TWO_POINTS);
	}

	@Test
	void twoClicksCreateCircle() {
		clickRW(1, -1);
		clickRW(2, -2);
		checkContent("A = (1, -1)", "B = (2, -2)", unicode("c: (x - 1)^2 + (y + 1)^2 = 2"));
	}

	@Test
	void clickingExistingPointsCreatesCircle() {
		add("A = (0, 0)");
		add("B = (2, 0)");
		clickRW(0, 0);
		clickRW(2, 0);
		checkContent("A = (0, 0)", "B = (2, 0)", unicode("c: x^2 + y^2 = 4"));
	}

	@Test
	void draggingCreatesCircle() {
		dragRW(1, -1, 2, -2);
		checkContent("A = (1, -1)", "B = (2, -2)", unicode("c: (x - 1)^2 + (y + 1)^2 = 2"));
	}

	@Test
	void circleFollowsItsDefiningPoint() {
		add("A = (1, -1)");
		add("B = (4, -1)");
		clickRW(1, -1);
		clickRW(4, -1);
		add("SetCoords(B, 1, -5)");
		GeoConic circle = (GeoConic) lookup("c");
		assertEquals(4, circle.getHalfAxis(0), 1e-8);
		assertEquals(1, circle.getMidpoint2D().getX(), 1e-8);
		assertEquals(-1, circle.getMidpoint2D().getY(), 1e-8);
	}
}
