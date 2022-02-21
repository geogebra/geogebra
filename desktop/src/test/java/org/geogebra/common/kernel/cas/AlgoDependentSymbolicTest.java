package org.geogebra.common.kernel.cas;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.junit.Assert;
import org.junit.Test;

public class AlgoDependentSymbolicTest extends BaseSymbolicTest {

	@Test
	public void testDependentMultivariateFunction() {
		t("a = 5", "5");
		t("f(a, x) = sqrt(a - x)", "sqrt(-x + 5)");
		String xml = app.getXML();
		Assert.assertTrue(xml.contains("a,x"));
		app.setXML(xml, true);
		Assert.assertEquals("f(a, x) = sqrt(-x + 5)", app.getKernel()
				.lookupLabel("f").toString(StringTemplate.defaultTemplate));
	}

	@Test
	public void testUndoRedoWorks() {
		LabelController controller = new LabelController();
		add("f(x, a) = x - a");
		GeoElement element = add("f(1, 2)");
		controller.hideLabel(element);
		element = add("f(3, 4)");
		controller.hideLabel(element);
		app.setXML(app.getXML(), true);
	}

	@Test
	public void testRedefinitionKeepsConstant() {
		add("f(x) = Integral(Integral(x))");
		// redefine geo
		add("f(x) = Integral(Integral(x))");
		GeoElement element = lookup("c_3");
		assertThat(element, is(nullValue()));
	}

	@Test
	public void testUndoRedoConstant() {
		add("Invert(sin(x))");
		GeoElement constant = lookup("k_1");
		assertThat(constant, is(notNullValue()));
		constant = lookup("k_2");
		assertThat(constant, is(nullValue()));

		app.setXML(app.getXML(), true);

		constant = lookup("k_2");
		assertThat(constant, is(nullValue()));
	}
}
