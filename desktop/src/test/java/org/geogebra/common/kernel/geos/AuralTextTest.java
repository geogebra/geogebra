package org.geogebra.common.kernel.geos;

import org.geogebra.commands.AlgebraTest;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.headless.AppDNoGui;
import org.junit.Assert;
import org.junit.Test;

public class AuralTextTest {

	static AppDNoGui app = AlgebraTest.createApp();

	private static void aural(String in, String... out) {
		GeoElementND[] geos = add(in);
		String aural = geos[0].getAuralText(new ScreenReaderBuilderDot());
		Log.debug("aural = " + aural);
		String[] sentences = aural.split("\\.");
		Assert.assertTrue(aural.endsWith("."));
		Assert.assertEquals(out.length, sentences.length);
		for (int i = 0; i < out.length; i++) {
			if (!sentences[i].matches(".*" + out[i] + ".*")) {
				Assert.assertEquals(out[i], sentences[i]);
			}
		}
	}

	private static GeoElementND[] add(String in) {
		return app.getKernel().getAlgebraProcessor().processAlgebraCommand(in,
				true);
	}

	@Test
	public void pointAural() {
		aural("(1,1)", "Point", "arrow", "edit");
		aural("Point(xAxis)", "Point", "plus and minus", "edit");
	}

	@Test
	public void point3DAural() {
		aural("(1,1,1)", "Point", "arrow", "edit");
		aural("Point(zAxis)", "Point", "plus and minus", "edit");
	}

	@Test
	public void numberAural() {
		aural("sl=Slider(-5,5)", "Slider", "start animation", "increase",
				"decrease", "edit");
		Assert.assertEquals("sl = 0",
				((GeoNumeric) get("sl")).getAuralCurrentValue());
		aural("4", "Number");
	}

	private GeoElementND get(String string) {
		return app.getKernel().lookupLabel(string);
	}

	@Test
	public void numberCaptionAural() {
		add("vec=Slider(-5,5)");
		add("SetCaption(vec,\"Vector v = %v\")");
		aural("vec", "Vector v = 0", "start animation", "increase",
				"decrease", "edit");
		Assert.assertEquals("Vector v = 0",
				((GeoNumeric) get("vec")).getAuralCurrentValue());
	}

	@Test
	public void checkboxAural() {
		aural("checkbox()", "Checkbox", "uncheck", "edit");
		aural("false", "Checkbox", " check", "edit");
	}

	@Test
	public void textAural() {
		aural("LaTeX(\"a+\\mathbf{x^2}\")", "a+x^(2)", "edit");
		aural("LaTeX(\"\\sqrt{x}\")", "sqrt(x)", "edit");
		aural("LaTeX(\"\\sqrt[3]{x}\")", "nroot(x,3)", "edit");
		aural("LaTeX(\"\\frac{x}{2}\")", "(x)/(2)", "edit");
		aural("LaTeX(\"\\vec{x}\")", " vector x", "edit");
		aural("LaTeX(\"\\fgcolor{red}{\\text{red text}}\")", "red text",
				"edit");
		aural("LaTeX(\"\\bgcolor{red}{\\text{not red text}}\")", "not red text",
				"edit");
		aural("TableText({{1,2,3},{3,4,5}})", "\\{\\{1,2,3\\},\\{3,4,5\\}\\}",
				"edit");
		aural("FractionText(1.5)", "(3)/(2)", "edit");
		aural("LaTeX(\"\\scalebox{0.5}{hello}\")", "hello", "edit");
		aural("LaTeX(\"\\rotatebox{90}{hello}\")", "hello", "edit");
		aural("LaTeX(\"\\mathsf{mathsf} \\sf{sf}\")", "mathsfsf", "edit");
		aural("LaTeX(\"\\mathit{mathit} \\it{it}\")", "mathitit", "edit");
		aural("LaTeX(\"\\mathtt{mathtt} \\tt{tt}\")", "mathtttt", "edit");
		aural("LaTeX(\"\\mathbf{mathbf} \\bf{bf}\")", "mathbfbf", "edit");
	}
}
