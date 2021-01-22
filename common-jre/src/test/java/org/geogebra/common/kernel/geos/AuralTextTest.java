package org.geogebra.common.kernel.geos;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.util.debug.Log;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AuralTextTest {

	static AppCommon3D app;

	@Before
	public void startApp() {
		app = new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
	}

	private static void aural(String in, String... out) {
		GeoElementND[] geos = add(in);
		String aural = geos[0].getAuralText(new ScreenReaderBuilderDot());
		Log.debug("aural = " + aural);
		String[] sentences = aural.split("\\.");
		Assert.assertTrue(aural.endsWith("."));
		assertEquals(out.length, sentences.length);
		for (int i = 0; i < out.length; i++) {
			if (!sentences[i].matches(".*" + out[i] + ".*")) {
				assertEquals(out[i], sentences[i]);
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
		assertEquals("sl = 0",
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
		assertEquals("Vector v = 0",
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
		aural("LaTeX(\"a_{bcd}\")", "a subscript bcd", "edit");
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
		aural("LaTeX(\"\\textsf{textsf} \\mathsf{mathsf} \\sf{sf}\")",
				"textsfmathsfsf", "edit");
		aural("LaTeX(\"\\textit{textit} \\mathit{mathit} \\it{it}\")",
				"textitmathitit", "edit");
		aural("LaTeX(\"\\texttt{texttt} \\mathtt{mathtt} \\tt{tt}\")",
				"textttmathtttt", "edit");
		aural("LaTeX(\"\\textbf{textbf} \\mathbf{mathbf} \\bf{bf}\")",
				"textbfmathbfbf", "edit");
		aural("LaTeX(\"\\textsc{textsc} \\sc{sc}\")", "textscsc", "edit");
		aural("LaTeX(\"nothing follows: \\phantom{shouldn't be read}\")",
				"nothingfollows:", "edit");
		aural("LaTeX(\"nothing follows: \\vphantom{shouldn't be read}\")",
				"nothingfollows:", "edit");
		aural("LaTeX(\"nothing follows: \\hphantom{shouldn't be read}\")",
				"nothingfollows:", "edit");
		aural("LaTeX(\"\\xleftrightarrow{p}j\")", "pj", "edit");
		aural("LaTeX(\"\\underrightarrow{p}j\")", "pj", "edit");
		aural("LaTeX(\"\\overrightarrow{p}j\")", "pj", "edit");
		aural("LaTeX(\"\\widehat{p}\")", "p with \u0302", "edit");
		aural("LaTeX(\"\\underline{p}j\")", "pj", "edit");

	}

	@Test
	public void readLaTeXCaption() {
		GeoElementND[] pointA = add("A = (1,2)");
		pointA[0].setCaption("$ \\sqrt {x}$");
		auralWhichContainsTheOutput("A", "sqrt(x)");
		GeoElementND[] pointB = add("B = (2,2)");
		pointB[0].setCaption(" $ \\text{this is my nice caption}$");
		auralWhichContainsTheOutput("B", "this is my nice caption");
	}

	@Test
	public void inputBoxShouldReadCaptionOrLabel() {
		GeoInputBox box = (GeoInputBox) add("myBox=InputBox()")[0];
		assertEquals("Input Box myBox", box.getAuralText().trim());
		box.setCaption("$\\frac{1}{2}$");
		assertEquals("Input Box (1)/(2)", box.getAuralText().trim());
		box.setCaption("plainText");
		assertEquals("Input Box plainText", box.getAuralText().trim());
	}

	@Test
	public void inputBoxShouldNotReadHiddenLabel() {
		GeoInputBox box = (GeoInputBox) add("myBox=InputBox()")[0];
		box.setLabelVisible(false);
		assertEquals("Input Box", box.getAuralText().trim());
		box.setCaption("$\\frac{1}{2}$");
		assertEquals("Input Box (1)/(2)", box.getAuralText().trim());
	}

	private static void auralWhichContainsTheOutput(String in, String... out) {
		GeoElementND[] geos = add(in);
		String aural = geos[0].getAuralText(new ScreenReaderBuilderDot());
		Log.debug("aural = " + aural);
		String[] sentences = aural.split("\\.");
		Assert.assertTrue(aural.endsWith("."));
		if (out[0].matches(".*\\(.*")) {
			out[0] = out[0].replace("(", "\\(");
		}
		if (out[0].matches(".*\\).*")) {
			out[0] = out[0].replace(")", "\\)");
		}
		if (!sentences[0].matches(".*" + out[0] + ".*")) {
			Assert.fail();
		}
	}
}
