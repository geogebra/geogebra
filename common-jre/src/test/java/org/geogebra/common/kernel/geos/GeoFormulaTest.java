package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.junit.Test;

public class GeoFormulaTest extends BaseUnitTest {

	@Test
	public void formulaCorrectlySavedAndLoaded() {
		final double x = 1.2;
		final double y = 2.5;
		final int width = 1848;
		final int height = 1956;
		final double angle = 2.7182;
		final String content = "e^(i*pi)+1=1";

		Construction cons = getApp().getKernel().getConstruction();

		GPoint2D startPoint = new GPoint2D(x, y);

		GeoFormula savedFormula = new GeoFormula(cons, startPoint);
		savedFormula.setWidth(width);
		savedFormula.setHeight(height);
		savedFormula.setAngle(angle);
		savedFormula.setContent(content);
		savedFormula.setLabel("testFormula");

		String appXML = getApp().getXML();
		XmlTestUtil.testCurrentXML(getApp());
		getApp().setXML(appXML, true);

		GeoFormula loadedFormula = (GeoFormula) lookup("testFormula");

		assertEquals(x, loadedFormula.getLocation().getX(), Kernel.MAX_PRECISION);
		assertEquals(y, loadedFormula.getLocation().getY(), Kernel.MAX_PRECISION);
		assertEquals(width, loadedFormula.getWidth(), Kernel.MAX_PRECISION);
		assertEquals(height, loadedFormula.getHeight(), Kernel.MAX_PRECISION);
		assertEquals(angle, loadedFormula.getAngle(), Kernel.MAX_PRECISION);
		assertEquals(content, loadedFormula.getContent());
	}
}
