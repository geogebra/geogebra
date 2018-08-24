package org.geogebra.io.latex;

import org.geogebra.common.main.ScreenReader;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.controller.ExpressionReader;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorScreenReaderTest {

	private static Parser parser;
	private static AppDNoGui app;

	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderDesktop());
		}
		app = new AppDNoGui(new LocalizationD(3), false);
		MetaModel m = new MetaModel();
		parser = new Parser(m);
	}

	@Test
	public void testReaderQuadratic() {
		checkReader("1+x^2", "start of 1 plus x squared", "after 1 before plus",
				"after plus before x", "after x before superscript",
				"start of superscript before 2", "end of superscript after 2",
				"end of 1 plus x squared");
	}

	@Test
	public void testReaderPower() {
		checkReader("x^3+x^4+1",
				"start of x cubed plus x start superscript 4 end superscript plus 1",
				"after x before superscript", "start of superscript before 3",
				"end of superscript after 3", "after x cubed before plus",
				"after plus before x", "after x before superscript",
				"start of superscript before 4", "end of superscript after 4",
				"after x start superscript 4 end superscript before plus",
				"after plus before 1",
				"end of x cubed plus x start superscript 4 end superscript plus 1");
	}

	@Test
	public void testIncompletePower() {
		checkReader("x^3+", "start of x cubed plus",
				"after x before superscript", "start of superscript before 3",
				"end of superscript after 3", "after x cubed before plus",
				"end of x cubed plus");
	}

	@Test
	public void testIncompleteFraction() {
		checkReader("x^3/()",
				"start of start fraction x cubed over end fraction",
				"start of numerator before x", "after x before superscript",
				"start of superscript before 3", "end of superscript after 3",
				"end of numerator after x cubed", "empty denominator",
				"end of start fraction x cubed over end fraction");
	}

	@Test
	public void testIncompleteSqrt() {
		checkReader("sqrt(x+)",
				"start of start square root x plus end square root",
				"start of square root before x", "after x before plus",
				"end of square root after plus",
				"end of start square root x plus end square root");
	}

	@Test
	public void testSin() {
		checkReader("sin(x+1)",
				"start of sin open parenthesis x plus 1 close parenthesis",
				"before sin", "after s before in", "after si before n",
				"after sin", "before x", "after x before plus",
				"after plus before 1", "after 1",
				"end of sin open parenthesis x plus 1 close parenthesis");
	}

	@Test
	public void testMinusSin() {
		checkReader("3-sin(x)",
				"start of 3 minus open parenthesis sin open parenthesis x close parenthesis close parenthesis",
				"after 3 before -", "after - before function", "before sin",
				"after s before in", "after si before n",
				"after sin", "before x", "after x",
				"end of 3 minus open parenthesis sin open parenthesis x close parenthesis close parenthesis");
	}

	@Test
	public void testCbrt() {
		checkReader("cbrt(x+1)",
				"start of start cube root x plus 1 end cube root",
				"before cbrt", "after c before brt", "after cb before rt",
				"after cbr before t",
				"after cbrt", "before x", "after x before plus",
				"after plus before 1", "after 1",
				"end of start cube root x plus 1 end cube root");
	}

	@Test
	public void testNroot() {
		checkReader("nroot(x, 4)",
				"start of start 4 root x end root",
				"start of index before 4", "end of index after 4",
				"start of radicand before x",
				"end of radicand after x",
				"end of start 4 root x end root");
	}

	@Test
	public void testNrootIncomplete() {
		checkReader("nroot(x+, 4)", "start of start 4 root x plus end root",
				"start of index before 4", "end of index after 4",
				"start of radicand before x", "after x before plus",
				"end of radicand after plus",
				"end of start 4 root x plus end root");
	}

	@Test
	public void testAbs() {
		checkReader("abs(x+1)",
				"start of start absolute value x plus 1 end absolute value",
				"start of absolute value before x", "after x before plus",
				"after plus before 1", "end of absolute value after 1",
				"end of start absolute value x plus 1 end absolute value");
	}

	@Test
	public void testReaderSqrt() {
		checkReader("1+sqrt(x^2+2x+1/x+33)",
				"start of 1 plus start square root x squared plus 2 times x plus start fraction 1 over x end fraction plus 33 end square root",
				"after 1 before plus", "after plus before square root",
				"start of square root before x( squared)?",
				"after x before superscript", "start of superscript before 2",
				"end of superscript after 2", "after x squared before plus",
				"after plus before 2( times )?x", "after 2 before x",
				"after 2( times )?x before plus", "after plus before fraction",
				"start of numerator before 1", "end of numerator after 1",
				"start of denominator before x", "end of denominator after x",
				"after fraction before plus", "after plus before 33",
				"after 3 before 3", "end of square root after 33",
				"end of 1 plus start square root x squared plus 2 times x plus start fraction 1 over x end fraction plus 33 end square root");
	}

	@Test
	public void testBrackets() {
		checkReader("2*(3+4)",
				"start of 2 times open parenthesis 3 plus 4 close parenthesis",
				"after 2 before *", "after * before parentheses",
				"start of parentheses before 3",
				"after 3 before plus", "after plus before 4",
				"end of parentheses after 4",
				"end of 2 times open parenthesis 3 plus 4 close parenthesis");
	}

	private static void checkReader(String input, String... output) {
		MathFormula mf = SerializeLaTeX.checkLaTeXRender(parser, input);

		final MathFieldD mathField = new MathFieldD();
		MathFieldInternal mfi = new MathFieldInternal(mathField);
		mfi.setFormula(mf);
		CursorController.firstField(mfi.getEditorState());
		mfi.update();
		ExpressionReader er = ScreenReader.getExpressionReader(app);
		for (int i = 0; i < output.length; i++) {
			String readerOutput = mfi.getEditorState().getDescription(er)
					.replaceAll(" +", " ");
			if (!readerOutput.matches(output[i])) {
				Assert.assertEquals(output[i], readerOutput);
			}
			CursorController.nextCharacter(mfi.getEditorState());
			mfi.update();
		}
	}
}
