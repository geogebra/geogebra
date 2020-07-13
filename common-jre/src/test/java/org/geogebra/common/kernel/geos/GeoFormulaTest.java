package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.io.XmlTestUtil;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.junit.Before;
import org.junit.Test;

public class GeoFormulaTest extends BaseUnitTest {

	private Construction cons;
	private GeoFormula equationEditor;

	@Before
	public void setupTest() {
		cons = getApp().getKernel().getConstruction();
		equationEditor = new GeoFormula(cons, null);
	}

	@Test
	public void formulaCorrectlySavedAndLoaded() {
		final double x = 1.2;
		final double y = 2.5;
		final int width = 1848;
		final int height = 1956;
		final double angle = 2.7182;
		final String content = "e^(i*pi)+1=1";

		GPoint2D startPoint = new GPoint2D(x, y);

		GeoFormula savedFormula = new GeoFormula(cons, startPoint);
		savedFormula.setSize(width, height);
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

	@Test
	public void definiteIntegral() {
		equationEditor.setContent("$defint(0,1)xdx");
		assertEquals("\\int_0^1{}xdx",
				equationEditor.toValueString(StringTemplate.latexTemplate));
	}

	@Test
	public void limes() {
		equationEditor.setContent("$limeq(x→∞)x^(2)");
		assertEquals("\\lim_{x\\rightarrow{}\\infty{}} x^{2}",
				equationEditor.toValueString(StringTemplate.latexTemplate));
	}

	@Test
	public void product() {
		equationEditor.setContent("$prodeq(i=1,10)x_{i}");
		assertEquals("\\prod_{i\\,=\\,1}^{10}{}x_{i}",
				equationEditor.toValueString(StringTemplate.latexTemplate));
	}

	@Test
	public void sum() {
		equationEditor.setContent("$sumeq(i=0,10)i");
		assertEquals("\\sum_{i\\,=\\,0}^{10}{}i",
				equationEditor.toValueString(StringTemplate.latexTemplate));
	}

	@Test
	public void vector() {
		equationEditor.setContent("$vec(AB)=b-a");
		assertEquals("\\overrightarrow{AB}\\,=\\,b-a",
				equationEditor.toValueString(StringTemplate.latexTemplate));
	}
}