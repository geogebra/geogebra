package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
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
		addSingle("f(x, a) = x - a");
		GeoElement element = addSingle("f(1, 2)");
		controller.hideLabel(element);
		element = addSingle("f(3, 4)");
		controller.hideLabel(element);
		app.setXML(app.getXML(), true);
	}

	private GeoElementND[] add(String string) {
		return app.getKernel().getAlgebraProcessor().processAlgebraCommand(string,
				true);
	}

	private GeoElement addSingle(String string) {
		return (GeoElement) add(string)[0];
	}
}
