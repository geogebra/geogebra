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
 
package org.geogebra.common.kernel.geos;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.jupiter.api.Test;

/**
 * Test for default labeling done by {@link GeoElement#getDefaultLabel()} and
 * {@link LabelManager}}
 */
class LabelManagerTest extends BaseUnitTest {

	@Test
	void implicitEquationsShouldHaveEquationPrefix() {
		GeoElement lineX = addSilent("x=y");
		assertEquals("eq1", lineX.getDefaultLabel());
		GeoElement lineXY = addSilent("y=x+y");
		assertEquals("eq1", lineXY.getDefaultLabel());
	}

	@Test
	void explicitEquationsShouldHaveFunctionLabels() {
		GeoElement lineY = addSilent("y=x");
		assertEquals("f", lineY.getDefaultLabel());
		GeoElement cubic = addSilent("y=x^3");
		assertEquals("f", cubic.getDefaultLabel());
		GeoElement trig = addSilent("y=sin(x)");
		assertEquals("f", trig.getDefaultLabel());
		GeoElement simpleFunction = addSilent("x");
		assertEquals("f", simpleFunction.getDefaultLabel());
	}

	@Test
	void explicitConicsShouldHaveFunctionLabels() {
		GeoElement parabola = addSilent("y=x^2");
		assertEquals("f", parabola.getDefaultLabel());
	}

	@Test
	void implicitConicsShouldHaveEquationPrefix() {
		GeoElement parabola = addSilent("x=y^2");
		assertEquals("eq1", parabola.getDefaultLabel());
	}

	@Test
	void toolConicsShouldHaveConicLabel() {
		GeoElement parabola = addSilent("Circle(O, 1)");
		assertEquals("c", parabola.getDefaultLabel());
	}

	@Test
	void equationLabelsShouldBeIndexed() {
		add("x=y");
		GeoElement lineX = addSilent("x=y");
		assertEquals("eq2", lineX.getDefaultLabel());
	}

	@Test
	void functionLabelsShouldBeSequential() {
		add("y=x");
		add("y=x^3");
		add("x");
		add("y=sin(x)");
		assertArrayEquals(new String[] { "f", "g", "h", "p" },
				getApp().getGgbApi().getAllObjectNames());
	}

	private GeoElement addSilent(String string) {
		GeoElement geo = add(string);
		geo.remove();
		return geo;
	}
}
