package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.junit.Assert;
import org.junit.Test;

public class AlgoDependentSymbolicTest extends BaseSymbolicTest {

	@Test
	public void testDependentMultivariateFunction() {
		t("a = 5", "5");
		t("f(a, x) = sqrt(a - x)", "sqrt(-x + 5)");
		String xml = app.getXML();
		Assert.assertTrue(xml.contains("f(a, x)"));
		app.setXML(xml, true);
		Assert.assertEquals("f(a, x) = sqrt(-x + 5)", app.getKernel()
				.lookupLabel("f").toString(StringTemplate.defaultTemplate));
	}
}
