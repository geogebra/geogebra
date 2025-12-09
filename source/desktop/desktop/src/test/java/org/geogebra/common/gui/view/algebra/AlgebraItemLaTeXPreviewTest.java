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
		assertEquals("f\\left(x \\right)\\, = \\,x + 1",
				AlgebraItem.getPreviewLatexForGeoElement(function));
	}

	@Test
	public void testTextLatexPreview() {
		GeoElement geo = add("t = \"text\"");
		new LabelController().hideLabel(geo);
		assertEquals("text", AlgebraItem.getPreviewLatexForGeoElement(geo));
	}
}
