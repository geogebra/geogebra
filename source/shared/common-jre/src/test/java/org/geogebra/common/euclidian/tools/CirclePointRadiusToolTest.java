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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_CIRCLE_POINT_RADIUS;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.test.annotation.Issue;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CirclePointRadiusToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_CIRCLE_POINT_RADIUS);
	}

	@Test
	void clickAndRadiusInputCreatesCircle() {
		prepareInput("3");
		clickRW(2, -2);
		checkContent("A = (2, -2)", unicode("c: (x - 2)^2 + (y + 2)^2 = 9"));
	}

	@Test
	void clickingExistingPointCreatesCircle() {
		add("A = (0, 0)");
		prepareInput("2");
		clickRW(0, 0);
		checkContent("A = (0, 0)", unicode("c: x^2 + y^2 = 4"));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"garbage(", "(1, 2)", "\"text\""})
	@Issue("APPS-7915")
	void invalidOrCancelledRadiusDoesNotCreateCircle(@Nullable String input) {
		prepareInput(input);
		assertDoesNotThrow(() -> clickRW(2, -2));
		checkContent("A = (2, -2)");
		assertFalse(getConstruction().isSuppressLabelsActive());
		assertEquals(MODE_CIRCLE_POINT_RADIUS, getApp().getMode());
		prepareInput("2");
		clickRW(6, -2);
		checkContent("A = (2, -2)", "B = (6, -2)", unicode("c: (x - 6)^2 + (y + 2)^2 = 4"));
	}

	@Test
	void dependentRadiusAndCenterUpdateCircle() {
		add("r = 3");
		add("A = (1, -1)");
		prepareInput("r + 1");
		clickRW(1, -1);
		GeoConic circle = (GeoConic) lookup("c");
		assertEquals(4, circle.getHalfAxis(0), 1e-8);
		add("SetValue(r, 5)");
		add("SetCoords(A, 2, -3)");
		assertEquals(6, circle.getHalfAxis(0), 1e-8);
		assertEquals(2, circle.getMidpoint2D().getX(), 1e-8);
		assertEquals(-3, circle.getMidpoint2D().getY(), 1e-8);
	}

	@Test
	void negativeRadiusCreatesUndefinedCircle() {
		prepareInput("-2");
		clickRW(1, -1);
		assertFalse(lookup("c").isDefined());
	}

	@Test
	void zeroRadiusCreatesSinglePointConic() {
		prepareInput("0");
		clickRW(1, -1);
		GeoConic circle = (GeoConic) lookup("c");
		assertTrue(circle.isDefined());
		assertEquals(GeoConicNDConstants.CONIC_SINGLE_POINT, circle.getType());
		assertEquals(0, circle.getHalfAxis(0), 1e-8);
	}
}
