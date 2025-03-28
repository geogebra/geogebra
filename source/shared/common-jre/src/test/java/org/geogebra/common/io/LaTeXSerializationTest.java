package org.geogebra.common.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.text.Normalizer;

import org.geogebra.common.util.debug.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.Korean;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.share.util.Unicode;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.serialize.ListSerializationAdapter;
import com.himamis.retex.renderer.share.serialize.TeXAtomSerializer;

public class LaTeXSerializationTest {
	private static Parser parser;

	/**
	 * Initialize parser and serializer.
	 */
	@BeforeClass
	public static void prepare() {
		if (FactoryProvider.getInstance() == null) {
			FactoryProvider.setInstance(new FactoryProviderCommon());
		}
		MetaModel m = new MetaModel();
		parser = new Parser(m);
	}

	@Test
	public void testAtoms() {
		checkCanonical("a", "a");
	}

	@Test
	public void testExpr() {
		checkCanonical("1 * 2", "1 * 2");
		checkCanonical("1 == 2", "1 == 2");
		checkCanonical("1 " + Unicode.PARALLEL + " 2",
				"1 " + Unicode.PARALLEL + " 2");
		checkCanonical("1 = 2", "1 = 2");
		checkCanonical("(1 * 2)", "(1 * 2)");
		checkCanonical("(1*2)", "(1*2)");
	}

	@Test
	public void testExpressionSymbolicInputBox() {
		checkCanonicalInputBox("[1,2]", "(1,2)");
		checkCanonicalInputBox("4-[1+[2+3]]", "4-(1+(2+3))");
	}

	@Test
	public void testSqrt() {
		checkCanonical("sqrt(x + 1)", "sqrt(x + 1)");
		checkCanonical("x sqrt(x + 1)", "x sqrt(x + 1)");
		checkCanonical("f(x) = sqrt(x)", "f(x) = sqrt(x)");
		checkCanonical("nroot(x + 1,3)", "nroot(x + 1,3)");
		checkCanonical("f(x) = nroot(x,3)", "f(x) = nroot(x,3)");
	}

	@Test
	public void testPrime() {
		checkCanonical("f'''(x)/2", "((f'''(x))/(2))");
	}

	@Test
	public void testInverseTrig() {
		checkCanonical("cos" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + "(1)/2",
				"((cos^(-1)(1))/(2))");
		checkCanonical("cos" + Unicode.SUPERSCRIPT_MINUS_ONE_STRING + " (1)/2",
				"cos^(-1) ((1)/(2))");
	}

	@Test
	public void testDiv() {
		checkCanonical("1/n^2", "((1)/(n^(2)))");
		checkCanonical("1/n_2", "((1)/(n_{2}))");
		checkCanonical("1/2", "((1)/(2))");
		checkCanonical("1/2+3", "((1)/(2))+3");
		checkCanonical("1/ ( 2)", "((1)/( 2))");
		checkCanonical("1/ (2+3)", "((1)/(2+3))");
		checkCanonical("1/ ((2+3)+4)", "((1)/((2+3)+4))");
		checkCanonical("1/(2/3)", "((1)/(((2)/(3))))");
		checkCanonical("x^2/ 3", "((x^(2))/(3))");
		checkCanonical("x^2 / 2", "((x^(2))/(2))");
		checkCanonical("2/cos(x)", "((2)/(cos(x)))");
		checkCanonical("1/(2^3)", "((1)/(2^(3)))");
		checkCanonical("1/2^3", "((1)/(2^(3)))");
		checkCanonical("1/2" + Unicode.SUPERSCRIPT_3, "((1)/(2^(3)))");
	}

	@Test
	public void testExponent() {
		checkCanonical("exp(-30)", "exp(-30)");
		checkCanonical(Unicode.EULER_STRING + "^-30",
				Unicode.EULER_STRING + "^(-30)");
		checkCanonical(Unicode.EULER_STRING + "^-30+1",
				Unicode.EULER_STRING + "^(-30)+1");
		checkCanonical(
				Unicode.EULER_STRING + Unicode.SUPERSCRIPT_MINUS
						+ Unicode.SUPERSCRIPT_1 + Unicode.SUPERSCRIPT_0,
				Unicode.EULER_STRING + "^(-10)");
	}

	@Test
	public void testFloorCeil() {
		checkCanonical("floor(x)", "floor(x)");
		checkCanonical("ceil(x)", "ceil(x)");
		checkCanonical(Unicode.LFLOOR + "x" + Unicode.RFLOOR, "floor(x)");
		checkCanonical(Unicode.LCEIL + "x" + Unicode.RCEIL, "ceil(x)");
	}

	@Test
	public void testPower() {
		checkCanonical("x ^ 2", "x ^(2)");
		checkCanonical("x ^ 2 ^3", "x ^(2) ^(3)");
		checkCanonical("(x ^ 2) ^3", "(x ^(2)) ^(3)");
		checkCanonical("x ^ 2 + 1", "x ^(2) + 1");
		checkCanonical("x" + Unicode.SUPERSCRIPT_2 + Unicode.SUPERSCRIPT_3,
				"x^(23)");
		checkCanonical("x" + Unicode.SUPERSCRIPT_MINUS + Unicode.SUPERSCRIPT_2
				+ Unicode.SUPERSCRIPT_3, "x^(-23)");
		checkCanonical("1 + x" + Unicode.SUPERSCRIPT_MINUS + Unicode.SUPERSCRIPT_2
				+ Unicode.SUPERSCRIPT_3, "1 + x^(-23)");
		checkCanonical("e^x*sin(x)", "e^(x)*sin(x)");
		checkCanonical("e^(-10/x)*sin(x)", "e^(-((10)/(x)))*sin(x)");
	}

	@Test
	public void testSubscript() {
		checkCanonical("x_2", "x_{2}");
		checkCanonical("x_2 = 7", "x_{2} = 7");
		checkCanonical("x_2 t", "x_{2} t");
		checkCanonical("x_2 sin(x)", "x_{2} sin(x)");
		checkCanonical("f_2(x)", "f_{2}(x)");
		checkCanonical("f_2 (x)", "f_{2} (x)");
	}

	@Test
	public void testPoint() {
		checkCanonical("(1,2)", "(1,2)");
		checkCanonical("(1;2)", "(1;2)");
		checkCanonical("(1,2,3)", "(1,2,3)");
		checkCanonical("(1;2;3)", "(1;2;3)");
	}

	@Test
	public void testMultiply() {
		checkCanonical("t (1,2)", "t (1,2)");
		checkCanonical("x x x", "x x x");
	}

	@Test
	public void testCommand() {
		checkCanonical("turtle1=Turtle[]", "turtle1=Turtle[]");
		checkCanonical("Turtle[]", "Turtle[]");
		checkCanonical("Turtle[1*3,7]", "Turtle[1*3,7]");
	}

	@Test
	public void testMatrix() {
		checkCanonical("{{1,2},{3,4}}", "{{1,2},{3,4}}");
		// normalize rows, but not cells
		checkCanonical("{{1 , 2} , { 3 , 4}}", "{{1 , 2},{ 3 , 4}}");
		checkCanonical("{{1 , 2} , 3}", "{{1 , 2} , 3}");
		checkCanonical("{{1,2},{3,4}}+1", "{{1,2},{3,4}}+1");
		checkCanonical("{7,{{1,2},{3,4}}+2,4,5,6}", "{7,{{1,2},{3,4}}+2,4,5,6}");
	}

	@Test
	public void testList() {
		checkCanonical("{x,1}", "{x,1}");
		checkCanonical("{x , 1}", "{x , 1}");
	}

	@Test
	public void testComma() {
		checkCanonical("If[x<1/x,x/2,sqrt(x/2)]",
				"If[x<((1)/(x)),((x)/(2)),sqrt(((x)/(2)))]");
		checkCanonical("(1;sqrt(2))", "(1;sqrt(2))");
		checkCanonical("(t^n;t)", "(t^(n);t)");
	}

	@Test
	public void testLog() {
		checkCanonical("log(10,x)", "log(10,x)");
		checkCanonical("log(x)", "log(x)");
	}

	@Test
	public void testParseLaTeX() {
		// Configuration.getFontMapping();
		checkLaTeX("4+x", "4+x");
		checkLaTeX("4-x", "4" + Unicode.MINUS + "x");
		checkLaTeX("\\frac{4}{x}", "(4)/(x)");
		checkLaTeX("4 \\times x", "4" + Unicode.MULTIPLY + "x");

		checkLaTeX("\\frac{x+y}{x-y}", "(x+y)/(x" + Unicode.MINUS + "y)");
		checkLaTeX("\\sqrt{x+y}", "sqrt(x+y)");
		checkLaTeX("\\sqrt{x}+2", "sqrt(x)+2");
		checkLaTeX("1-\\sqrt[3]{x}", "1" + Unicode.MINUS + "nroot(x,3)");
		checkLaTeX("X=\\left(x_0+2x_x,y_0+2x_y\\right)",
				"X=(x_0+2x_x,y_0+2x_y)");
		checkLaTeX("i=\\left[0,\\frac{6\\pi}{p}...24\\pi\\right]",
				"i=[0,(6pi)/(p)...24pi]".replace("pi", Unicode.PI_STRING));
		checkLaTeX(
				"\\left(\\left(1-t\\right)\\left(x_1\\right)+t\\left(x_1+R\\ "
						+ "f\\left(j\\right)\\right),\\left(1-t\\right)\\left(y_1\\right)"
						+ "+t\\left(y_1+Rg\\left(j\\right)\\right)\\right)",
				"((1" + Unicode.MINUS + "t)(x_1)+t(x_1+R f(j)),(1"
						+ Unicode.MINUS + "t)(y_1)+t(y_1+Rg(j)))");
		checkLaTeX("\\frac{x^2}{m^2}+\\frac{y^2}{n^2}\\ge2",
				"(x^(2))/(m^(2))+(y^(2))/(n^(2))" + Unicode.GREATER_EQUAL
						+ "2");
		checkLaTeX("a\\leq b", "a" + Unicode.LESS_EQUAL + "b");
		checkLaTeX("f\\left(x\\right)=\\sin\\left(x\\right)", "f(x)=sin(x)");
		checkLaTeX("r\\ =\\ g^{\\theta}",
				"r = g^(" + Unicode.theta_STRING + ")");
		checkLaTeX("7\\cdot 6", "7" + Unicode.MULTIPLY + "6");
		checkLaTeX("7\\times 6", "7" + Unicode.MULTIPLY + "6");
		checkLaTeX("\\left( \\alpha + \\beta \\right)",
				"(" + Unicode.alpha + "+" + Unicode.beta + ")");
		checkLaTeX("\\sqrt[3]{x}", "nroot(x,3)");
		checkLaTeX("{1}\\frac{2}{3}", "1(2)/(3)");
		checkLaTeX("\\sqrt{{3}\\frac{4}{5}}", "sqrt(3(4)/(5))");
		checkLaTeX("\\sqrt[4]{{3}\\frac{4}{5}}", "nroot(3(4)/(5),4)");
		checkLaTeX("5\\cdot {1}\\frac{2}{3}", "5" + Unicode.MULTIPLY + "1(2)/(3)");
		checkLaTeX("1.\\overline{34}", "1.3" + Unicode.OVERLINE + "4" + Unicode.OVERLINE);
		checkLaTeX("1.2\\overline{3}", "1.23" + Unicode.OVERLINE);
	}

	@Test
	public void testParseLaTeXAdapter() {
		checkLaTeX("a=\\left[1,...,4\\right]", "a=(1...4)",
				new ListSerializationAdapter());
		checkLaTeX("a=\\left[0.8,1.2,...,4\\right]",
				"a=Sequence[0.8,4,1.2-(0.8)]", new ListSerializationAdapter());
	}

	@Test
	public void testBinaryOp() {
		for (char op : new char[] { Unicode.LESS_EQUAL, Unicode.GREATER_EQUAL,
				Unicode.IS_SUBSET_OF, Unicode.IS_ELEMENT_OF,
				Unicode.IS_SUBSET_OF_STRICT }) {
			checkCanonical("5 " + op + " 3", "5 " + op + " 3");
			checkCanonical("5 " + op + " (2/3*x+5/3)",
					"5 " + op + " (((2)/(3))*x+((5)/(3)))");
		}
	}

	@Test
	public void testKoreanNormalization() {
		testKorean("\uD4DB");

		// Hangul syllables range
		// https://en.wikipedia.org/wiki/Hangul_Syllables
		for (char ch = '\uac00'; ch < '\ud7a3'; ch++) {
			testKorean(ch + "");
		}

		testKorean2(Korean.flattenKorean(
				"\uD56D\uC131\uC740 \uD56D\uC0C1 \uD63C\uC790 \uC788\uB294 \uAC83\uC774"
						+ " \uC544\uB2C8\uB77C, \uB450 \uAC1C \uC774\uC0C1\uC758"));

		for (char lead = '\u1100'; lead <= '\u1112'; lead++) {
			for (char vowel = '\u1161'; vowel <= '\u1175'; vowel++) {
				for (char tail = '\u11a8'; tail <= '\u11c2'; tail++) {
					// System.err.println(lead + " " + vowel + " " + tail);
					testKorean2(lead + "" + vowel + "" + tail);
				}
			}
		}
	}

	private static void testKorean2(String s) {
		String s1 = Normalizer.normalize(s, Normalizer.Form.NFKC);
		String s2 = Korean.unflattenKorean(s).toString();

		assertEquals(s1, s2);
	}

	private static void testKorean(String s) {
		assertEquals(Normalizer.normalize(s, Normalizer.Form.NFD),
				Korean.flattenKorean(s));
	}

	private static void checkLaTeX(String string, String string2) {
		checkLaTeX(string, string2, null);
	}

	private static void checkLaTeX(String string, String string2,
			ListSerializationAdapter ad) {
		TeXFormula tf = new TeXFormula(string);
		assertEquals(string2,
				new TeXAtomSerializer(ad).serialize(tf.root));
	}

	private static void checkCanonical(String input, String output) {
		checkCanonical(input, output, new GeoGebraSerializer(null));
	}

	private static void checkCanonical(String input, String output,
			GeoGebraSerializer geoGebraSerializer) {
		MathFormula mf = checkLaTeXRender(parser, input);
		assertNotNull(mf);
		assertEquals(mf.getRootComponent() + "", output,
				geoGebraSerializer.serialize(mf));
		checkLaTeXRender(parser, input);
	}

	private static void checkCanonicalInputBox(String input, String output) {
		GeoGebraSerializer geoGebraSerializer = new GeoGebraSerializer(null);
		geoGebraSerializer.forceRoundBrackets();
		checkCanonical(input, output, geoGebraSerializer);
	}

	/**
	 * Check that formula can be rendered without error
	 *
	 * @param parser2
	 *            parser
	 *
	 * @param input
	 *            input
	 * @return formula
	 * @throws com.himamis.retex.renderer.share.exception.ParseException
	 *             when formula can't be parsed
	 */
	static MathFormula checkLaTeXRender(Parser parser2, String input)
			throws com.himamis.retex.renderer.share.exception.ParseException {
		try {
			MathFormula mf = parser2.parse(input);
			assertNotNull(mf);
			String tex = TeXSerializer.serialize(mf.getRootComponent());
			// TeXFormula tf = new TeXFormula();
			TeXParser tp = new TeXParser(tex);
			tp.parse();
			return mf;
		} catch (ParseException e) {
			Log.debug(e);
			fail(e.getMessage());
		}
		return null;
	}
}
