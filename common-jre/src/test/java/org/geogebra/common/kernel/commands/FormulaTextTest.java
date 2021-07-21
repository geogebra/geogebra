package org.geogebra.common.kernel.commands;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Assert;
import org.junit.Test;

public class FormulaTextTest extends BaseUnitTest {

	@Test
	public void testUnitCoefficients() {
		// check that the AV preserves the input
		add("f(x) = sin(1x+1x-1x)");
		Assert.assertEquals("f(x) = sin(1x + 1x - 1x)",
				lookup("f").toString(StringTemplate.algebraTemplate));

		add("a = 1");
		add("f(x) = sin(ax+ax-ax)");
		Assert.assertEquals("f(x) = sin(1 x + 1 x - 1 x)",
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

}
