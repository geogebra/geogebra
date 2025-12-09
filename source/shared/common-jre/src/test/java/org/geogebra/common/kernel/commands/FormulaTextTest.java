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

package org.geogebra.common.kernel.commands;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.test.annotation.Issue;
import org.junit.Test;

public class FormulaTextTest extends BaseUnitTest {

	@Test
	public void testUnitCoefficients() {
		// check that the AV preserves the input
		add("f(x) = sin(1x+1x-1x)");
		assertEquals("f(x) = sin(1x + 1x - 1x)",
				lookup("f").toString(StringTemplate.algebraTemplate));

		add("a = 1");
		add("f(x) = sin(ax+ax-ax)");
		assertEquals("f(x) = sin(1 x + 1 x - 1 x)",
				lookup("f").toString(StringTemplate.algebraTemplate));

		// but FormulaText removes unit coefficients
		t("FormulaText(sin(1x+1x-1x))",
				"\\operatorname{sin} \\left( x + x - x \\right)");
		t("FormulaText(sin(ax+ax-ax))",
				"\\operatorname{sin} \\left( x + x - x \\right)");
		add("a = -1");
		t("FormulaText(sin(ax+ax-ax))",
				"\\operatorname{sin} \\left( -x - x + x \\right)");
		// except when substitute variables is false
		t("FormulaText(sin(ax+ax-ax), false)",
				"\\operatorname{sin} \\left( a \\; x + a \\; x - a \\; x \\right)");
	}

	@Test
	@Issue("APPS-5698")
	public void formulaWithTextEquations() {
		add("txt=\"3.14\"");
		add("num=22");
		add("eq:num/7=txt");
		assertThat(add("FormulaText(eq)"), hasValue("\\frac{22}{7}\\, = \\,3.14"));
	}
}
