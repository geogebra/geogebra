package org.geogebra.common.gui.view.algebra;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.kernel.geos.BaseSymbolicTest;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.scientific.LabelController;
import org.junit.Before;
import org.junit.Test;

public class AlgebraItemLaTeXPreviewTest extends BaseSymbolicTest {

	@Before
	public void clean() {
		app.getKernel().clearConstruction(true);
		app.setCasConfig();
		app.getKernel().setAngleUnit(app.getConfig().getDefaultAngleUnit());
	}

	@Test
	public void testCommandLatexPreview() {
		GeoElement integral = add("a(x) = Integral(x*x,1,2)");
		assertEquals(AlgebraItem.getPreviewLatexForGeoElement(integral), "a\\left(x "
				+ "\\right)\\, = \\,\\int\\limits_{1}^{2}x \\; x\\,\\mathrm{d}x");

		GeoElement solve = add("b(x) = Solve(x*x = 4)");
		assertEquals(AlgebraItem.getPreviewLatexForGeoElement(solve), "b\\left(x \\"
				+ "right)\\, = \\,Solve \\left(x \\; x\\, = \\,4 \\right)");
	}

	@Test
	public void testSimpleLatexPreview() {
		GeoElement geo = add("a = c + c");
		assertEquals(AlgebraItem.getPreviewLatexForGeoElement(geo), "a\\, = \\,c + c");

		GeoElement function = add("f(x) = x+1");
		assertEquals(AlgebraItem.getPreviewLatexForGeoElement(function), "f\\left(x \\"
				+ "right)\\, = \\,x + 1");
	}

	@Test
	public void testTextLatexPreview() {
		GeoElement geo = add("t = \"text\"");
		new LabelController().hideLabel(geo);
		assertEquals(AlgebraItem.getPreviewLatexForGeoElement(geo), "text");
	}
}
