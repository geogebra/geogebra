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

import static org.geogebra.common.euclidian.EuclidianConstants.MODE_ELLIPSE_THREE_POINTS;
import static org.geogebra.test.TestStringUtil.unicode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EllipseThreePointsToolTest extends BaseToolTest {

	@BeforeEach
	void setMode() {
		setMode(MODE_ELLIPSE_THREE_POINTS);
	}

	@Test
	void threeClicksCreateEllipse() {
		clickRW(0, 0);
		clickRW(2, 0);
		clickRW(1, -3);
		checkContent(
				"A = (0, 0)",
				"B = (2, 0)",
				"C = (1, -3)",
				"c: " + explicit("(x - 1)^2 * 9 + y^2 * 10 = 9 * 10"));
	}

	@Test
	void clickingExistingPointsCreatesEllipse() {
		add("A = (0, 0)");
		add("B = (2, 0)");
		add("C = (1, -3)");
		clickRW(0, 0);
		clickRW(2, 0);
		clickRW(1, -3);
		checkContent(
				"A = (0, 0)",
				"B = (2, 0)",
				"C = (1, -3)",
				"c: " + explicit("(x - 1)^2 * 9 + y^2 * 10 = 9 * 10"));
	}

	@Test
	void mixedNewAndExistingPointsShouldCreateEllipse() {
		add("B = (2, 0)");
		Set<String> existingObjects = getObjectNames();

		clickRW(0, 0);
		clickRW(2, 0);
		clickRW(1, -3);

		GeoConic ellipse = getOnlyNewObject(existingObjects, GeoConic.class);
		assertTrue(ellipse.isEllipse());
	}

	@Test
	void clickingOnLineCreatesFocusPointOnLine() {
		add("f: y = -1");
		clickRW(1, -1);
		clickRW(2, 0);
		clickRW(1, -3);
		assertEquals("Point(f)", lookup("A").getDefinition(StringTemplate.testTemplate));
		GeoConic ellipse = (GeoConic) lookup("c");
		assertTrue(ellipse.isDefined());
		assertTrue(ellipse.isEllipse());
	}

	@Test
	void collinearPointsCreateDoubleLine() {
		add("A = (0, 0)");
		add("B = (2, 0)");
		add("C = (1, 0)");
		clickRW(0, 0);
		clickRW(2, 0);
		clickRW(1, 0);
		GeoConic ellipse = (GeoConic) lookup("c");
		assertEquals(GeoConicNDConstants.CONIC_DOUBLE_LINE, ellipse.getType());
	}

	@Test
	void secondConstructionShouldStartWithEmptySelection() {
		clickRW(0, 0);
		clickRW(2, 0);
		clickRW(1, -3);
		Set<String> existingObjects = getObjectNames();

		clickRW(6, 0);
		clickRW(8, 0);
		clickRW(7, -3);

		GeoConic secondEllipse = getOnlyNewObject(existingObjects, GeoConic.class);
		assertTrue(secondEllipse.isEllipse());
	}

	private String explicit(String expression) {
		GeoConic c = (GeoConic)
				getApp().getKernel().getAlgebraProcessor().evaluateToGeoElement(expression, false);
		c.setToImplicitForm();
		return unicode(c.toValueString(StringTemplate.editTemplate));
	}
}
