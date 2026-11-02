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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_PARABOLA;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ParabolaToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_PARABOLA);
	}

	@Test
	void clickingLineThenPointCreatesParabola() {
		add("f: y = -1");
		add("A = (2, -2)");
		clickRW(1, -1);
		clickRW(2, -2);
		checkContent("f: y = -1", "A = (2, -2)", unicode("c: x^2 - 4x + 2y = -7"));
	}

	@Test
	void clickingPointThenLineCreatesParabola() {
		add("f: y = -1");
		add("A = (2, -2)");
		clickRW(2, -2);
		clickRW(1, -1);
		checkContent("f: y = -1", "A = (2, -2)", unicode("c: x^2 - 4x + 2y = -7"));
	}

	@Test
	void lineThenEmptySpaceShouldCreateParabola() {
		add("f: y = -1");
		Set<String> existingObjects = getObjectNames();

		clickRW(0, -1);
		clickRW(2, -2);

		GeoConic parabola = getOnlyNewObject(existingObjects, GeoConic.class);
		assertTrue(parabola.isParabola());
	}

	@ParameterizedTest
	@ValueSource(strings = {"Segment((-2, -1), (2, -1))", "Ray((-2, -1), (2, -1))"})
	void limitedPathShouldBeAcceptedAsDirectrix(String directrixDefinition) {
		add("f = " + directrixDefinition);
		Set<String> existingObjects = getObjectNames();

		clickRW(0, -1);
		clickRW(2, -2);

		GeoConic parabola = getOnlyNewObject(existingObjects, GeoConic.class);
		assertTrue(parabola.isParabola());
	}

	@Test
	void clickingOnConicIgnoresPathAndCreatesFreeFocusPoint() {
		add("k: x^2 + y^2 = 9");
		add("f: y = -5");
		clickRW(3, 0);
		clickRW(0, -5);
		GeoElement focus = lookup("A");
		assertFalse(focus.isPointOnPath());
		checkContent(
				unicode("k: x^2 + y^2 = 9"), "f: y = -5", "A = (3, 0)", unicode("c: x^2 - 6x - 10y = 16"));
	}

	@Test
	void clickingTwoPointsWithoutLineDoesNotCreateParabola() {
		clickRW(0, 0);
		clickRW(2, -2);
		checkContent("A = (0, 0)", "B = (2, -2)");
	}
}
