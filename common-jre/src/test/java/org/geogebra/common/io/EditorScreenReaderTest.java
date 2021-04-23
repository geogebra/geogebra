package org.geogebra.common.io;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.controller.CursorController;
import com.himamis.retex.editor.share.controller.ExpressionReader;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.platform.FactoryProvider;

public class EditorScreenReaderTest {

	private static Parser parser;
	private static AppCommon app;

	/**
	 * Initialize app and parser.
	 */
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
		app = AppCommonFactory.create3D();
		MetaModel m = new MetaModel();
		parser = new Parser(m);
	}

	@Test
	public void testReaderQuadratic() {
		checkReader("1+x^2", "start of formula 1 plus x squared",
				"after 1 before plus", "after plus before x",
				"after x before superscript", "start of superscript before 2",
				"end of superscript after 2",
				"end of formula 1 plus x squared");
	}

	@Test
	public void testReaderPower() {
		checkReader("x^3+x^4+1",
				"start of formula x cubed plus x to the power of 4 end power plus 1",
				"after x before superscript", "start of superscript before 3",
				"end of superscript after 3", "after x cubed before plus",
				"after plus before x", "after x before superscript",
				"start of superscript before 4", "end of superscript after 4",
				"after x to the power of 4 end power before plus",
				"after plus before 1",
				"end of formula x cubed plus x to the power of 4 end power plus 1");
	}

	@Test
	public void testIncompletePower() {
		checkReader("x^3+", "start of formula x cubed plus",
				"after x before superscript", "start of superscript before 3",
				"end of superscript after 3", "after x cubed before plus",
				"end of formula x cubed plus");
	}

	@Test
	public void testIncompleteFraction() {
		checkReader("x^3/()",
				"start of formula start fraction x cubed over end fraction",
				"start of numerator before x", "after x before superscript",
				"start of superscript before 3", "end of superscript after 3",
				"end of numerator after x cubed", "empty denominator",
				"end of formula start fraction x cubed over end fraction");
	}

	@Test
	public void testIncompleteSqrt() {
		checkReader("sqrt(x+)",
				"start of formula start square root x plus end square root",
				"start of square root before x", "after x before plus",
				"end of square root after plus",
				"end of formula start square root x plus end square root");
	}

	@Test
	public void testSin() {
		checkReader("sin(x+1)",
				"start of formula sin open parenthesis x plus 1 close parenthesis",
				"before sin", "after s before in", "after si before n",
				"after sin before parenthesis", "start of parentheses before x",
				"after x before plus", "after plus before 1",
				"end of parentheses after 1",
				"end of formula sin open parenthesis x plus 1 close parenthesis");
	}

	@Test
	public void testMinusSin() {
		checkReader("3-sin(x)",
				"start of formula 3 minus sin open parenthesis x close parenthesis",
				"after 3 before minus", "after minus before function",
				"before sin", "after s before in", "after si before n",
				"after sin before parenthesis", "start of parentheses before x",
				"end of parentheses after x",
				"end of formula 3 minus sin open parenthesis x close parenthesis");
	}

	@Test
	public void testPlusSin() {
		checkReader("3+sin(x)",
				"start of formula 3 plus sin open parenthesis x close parenthesis",
				"after 3 before plus", "after plus before function",
				"before sin", "after s before in", "after si before n",
				"after sin before parenthesis", "start of parentheses before x",
				"end of parentheses after x",
				"end of formula 3 plus sin open parenthesis x close parenthesis");
	}

	@Test
	public void testCbrt() {
		checkReader("cbrt(x+1)",
				"start of formula start cube root x plus 1 end cube root",
				"start of cube root before x", "after x before plus",
				"after plus before 1", "end of cube root after 1",
				"end of formula start cube root x plus 1 end cube root");
	}

	@Test
	public void testNroot() {
		checkReader("nroot(x,4)", "start of formula start 4th root x end root",
				"start of index before 4", "end of index after 4",
				"start of radicand before x", "end of radicand after x",
				"end of formula start 4th root x end root");
	}

	@Test
	public void testNrootIncomplete() {
		checkReader("nroot(x+,4)",
				"start of formula start 4th root x plus end root",
				"start of index before 4", "end of index after 4",
				"start of radicand before x", "after x before plus",
				"end of radicand after plus",
				"end of formula start 4th root x plus end root");
	}

	@Test
	public void testQuotes() {
		checkReader("\"a{b}c\"",
				"start of formula \"a\\{b\\}c\"",
				"start of quotes before a", "after a before \\{",
				"after \\{ before b", "after b before \\}",
				"after \\} before c",
				"end of quotes after c",
				"end of formula \"a\\{b\\}c\"");
	}

	@Test
	public void testAbs() {
		checkReader("abs(x+1)",
				"start of formula start absolute value x plus 1 end absolute value",
				"start of absolute value before x", "after x before plus",
				"after plus before 1", "end of absolute value after 1",
				"end of formula start absolute value x plus 1 end absolute value");
	}

	@Test
	public void testReaderSqrt() {
		checkReader("1+sqrt(x^2+2x+1/x+33)",
				"start of formula 1 plus start square root x squared plus 2 times"
						+ " x plus start fraction 1 over x end fraction plus 33 end square root",
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
				"end of formula 1 plus start square root x squared plus 2 times "
						+ "x plus start fraction 1 over x end fraction plus 33 end square root");
	}

	@Test
	public void testReaderSqrt2() {
		checkReader("sqrt(x)",
				"start of formula start square root x end square root",
				"start of square root before x", "end of square root after x",
				"end of formula start square root x end square root");
	}

	@Test
	public void testReaderSqrtPi() {
		checkReader("sqrt(" + Unicode.pi + ")",
				"start of formula start square root pi end square root",
				"start of square root before pi", "end of square root after pi",
				"end of formula start square root pi end square root");
	}

	@Test
	public void testBrackets() {
		checkReader("2*(3+4)-2",
				"start of formula 2 times open parenthesis 3 plus 4 close parenthesis minus 2",
				"after 2 before times", "after times before parenthesis",
				"start of parentheses before 3", "after 3 before plus",
				"after plus before 4", "end of parentheses after 4",
				"after parenthesis before minus", "after minus before 2",
				"end of formula 2 times open parenthesis 3 plus 4 close parenthesis minus 2");
	}

	@Test
	public void testBracketsIncomplete() {
		checkReader("3-()", "start of formula 3 minus empty parentheses",
				"after 3 before minus", "after minus before parenthesis",
				"empty parentheses",
				"end of formula 3 minus empty parentheses");
	}

	@Test
	public void testGreek() {
		checkReader("2*pi*x", "start of formula 2 times pi times x",
				"after 2 before times", "after times before pi",
				"after p before i", "after pi before times",
				"after times before x", "end of formula 2 times pi times x");
	}

	private static void checkReader(String input, String... output) {
		MathFormula mf = LaTeXSerializationTest.checkLaTeXRender(parser, input);

		SyntaxAdapterImpl adapter = new SyntaxAdapterImpl(app.kernel);
		final MathFieldCommon mathField = new MathFieldCommon(new MetaModel(), adapter);
		MathFieldInternal mfi = mathField.getInternal();
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
