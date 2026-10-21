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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GeoFunctionNVarTest extends BaseUnitTest {

	@Test
	void testNVarInequalityPreserved() {
		addAvInput("a: x < y");
		addAvInput("SetValue(a, ?)");
		getApp().setXML(getApp().getXML(), true);
		assertEquals("a: ?", lookup("a").toString(StringTemplate.defaultTemplate));
	}

	@Test
	void shouldShowAsUndefinedOnReload() {
		addAvInput("user(x,y)=x+y");
		addAvInput("def=IsDefined(user)");
		addAvInput("SetValue(user,?)");
		getApp().setXML(getApp().getXML(), true);
		assertEquals("def = false", lookup("def").toString(StringTemplate.defaultTemplate));
	}

	@Test
	@Issue("APPS-5759")
	void malformedFunctionCallShouldNotCrash() {
		add("eq1:x+y");
		GeoElement element = add("eq1(x=5 x^(2)-4 y^(2)=5)");
		assertNotNull(element.toOutputValueString(StringTemplate.testTemplate));
	}

	@Test
	void shouldNotInitInequalityIfNotNeeded() {
		add("k=1");
		GeoElement text = add("Text(y<k*x)");
		GeoFunctionNVar inequality =
				(GeoFunctionNVar) Objects.requireNonNull(text.getParentAlgorithm()).getInput(0);
		assertFalse(inequality.isInequality(), "Inequality flag should not be initialized");
	}

	@ParameterizedTest
	@ValueSource(strings = {"y<x", "y<k*x"})
	@Issue("APPS-8021")
	void shouldHaveCorrectStyleStandalone(String definition) {
		add("k=1");
		GeoElement function = add(definition);
		assertEquals(5, function.getLineThickness());
		assertEquals(.25, function.getAlphaValue(), Kernel.STANDARD_PRECISION);
		assertEquals(GColor.BLUE, function.getObjectColor());
	}

	@ParameterizedTest
	@ValueSource(strings = {"{y<x}", "{y<k*x}"})
	@Issue("APPS-8021")
	void shouldHaveCorrectStyleInList(String definition) {
		add("k=1");
		GeoElement function = add(definition);
		assertEquals(5, function.getLineThickness());
		assertEquals(.25, function.getAlphaValue(), Kernel.STANDARD_PRECISION);
		assertEquals(GeoGebraColorConstants.GGB_GREEN, function.getObjectColor());
	}
}
