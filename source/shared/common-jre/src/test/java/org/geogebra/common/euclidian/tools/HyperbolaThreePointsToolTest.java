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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_HYPERBOLA_THREE_POINTS;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoConic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HyperbolaThreePointsToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_HYPERBOLA_THREE_POINTS);
	}

	@Test
	void threeClicksCreateHyperbola() {
		clickRW(0, 0);
		clickRW(10, 0);
		clickRW(2, 0);
		checkContent(
				"A = (0, 0)",
				"B = (10, 0)",
				"C = (2, 0)",
				"c: " + explicit("-(x - 5)^2 * 16 + y^2 * 9 = -16 * 9"));
	}

	@Test
	void clickingExistingPointsCreatesHyperbola() {
		add("A = (0, 0)");
		add("B = (10, 0)");
		add("C = (2, 0)");
		clickRW(0, 0);
		clickRW(10, 0);
		clickRW(2, 0);
		checkContent(
				"A = (0, 0)",
				"B = (10, 0)",
				"C = (2, 0)",
				"c: " + explicit("-(x - 5)^2 * 16 + y^2 * 9 = -16 * 9"));
	}

	@Test
	void clickingOnCircleCreatesFocusPointOnCircle() {
		add("k: x^2 + y^2 = 9");
		clickRW(3, 0);
		clickRW(10, 0);
		clickRW(0, -5);
		assertEquals("Point(k)", lookup("A").getDefinition(StringTemplate.testTemplate));
		GeoConic hyperbola = (GeoConic) lookup("c");
		assertTrue(hyperbola.isDefined());
		assertTrue(hyperbola.isHyperbola());
	}

	private String explicit(String expression) {
		GeoConic c = (GeoConic)
				getApp().getKernel().getAlgebraProcessor().evaluateToGeoElement(expression, false);
		c.setToImplicitForm();
		return unicode(c.toValueString(StringTemplate.editTemplate));
	}
}
