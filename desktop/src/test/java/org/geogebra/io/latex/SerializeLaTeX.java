package org.geogebra.io.latex;

import org.geogebra.common.io.latex.BracketsAdapter;
import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.io.latex.ParseException;
import org.geogebra.common.io.latex.Parser;
import org.geogebra.common.io.latex.TeXAtomSerializer;
import org.geogebra.common.util.lang.Unicode;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.desktop.MathFieldD;
import com.himamis.retex.editor.share.controller.EditorState;
import com.himamis.retex.editor.share.controller.InputController;
import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.Korean;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXParser;
import com.himamis.retex.renderer.share.platform.FactoryProvider;


public class SerializeLaTeX {
	static Parser parser;
	private static GeoGebraSerializer serializer;

	@BeforeClass
	public static void prepare() {
		FactoryProvider.setInstance(new FactoryProviderDesktop());
		MetaModel m = new MetaModel();
		parser = new Parser(m);
		serializer = new GeoGebraSerializer();
	}

	@Test
	public void testAtoms() {
		checkCannon("a", "a");
	}

	@Test
	public void testExpr() {
		checkCannon("1 * 2", "1*2");
		checkCannon("1 == 2", "1==2");
		checkCannon("1 = 2", "1=2");
		checkCannon("(1 * 2)", "(1*2)");

	}

	@Test
	public void testSqrt() {
		checkCannon("sqrt(x + 1)", "sqrt(x+1)");
		checkCannon("f(x) = sqrt(x)", "f(x)=sqrt(x)");
		checkCannon("nroot(x + 1,3)", "nroot(x+1,3)");
		checkCannon("f(x) = nroot(x,3)", "f(x)=nroot(x,3)");

	}

	@Test
	public void testDiv() {
		checkCannon("1/2", "(1)/(2)");
		checkCannon("1/2+3", "(1)/(2)+3");
		checkCannon("1/ ( 2)", "(1)/(2)");
		checkCannon("1/ ( 2+3)", "(1)/(2+3)");
		checkCannon("1/ ((2+3)+4)", "(1)/((2+3)+4)");
		checkCannon("1/(2/3)", "(1)/((2)/(3))");
		checkCannon("x^2/ 3", "(x^(2))/(3)");
		checkCannon("x^2 / 2", "(x^(2))/(2)");
		checkCannon("2/cos(x)", "(2)/(cos(x))");
	}

	@Test
	public void testExponent() {
		checkCannon("exp(-30)", "exp(-30)");
		checkCannon(Unicode.EULER_STRING + "^-30", Unicode.EULER_STRING
				+ "^(-30)");
		checkCannon(Unicode.EULER_STRING + "^-30+1", Unicode.EULER_STRING
				+ "^(-30)+1");
		checkCannon(Unicode.EULER_STRING + Unicode.Superscript_Minus
				+ Unicode.Superscript_1 + Unicode.Superscript_0,
				Unicode.EULER_STRING + "^(-10)");

	}

	@Test
	public void testFloorCeil() {
		checkCannon("floor(x)", "floor(x)");
		checkCannon("ceil(x)", "ceil(x)");
		checkCannon(Unicode.LFLOOR + "x" + Unicode.RFLOOR, "floor(x)");
		checkCannon(Unicode.LCEIL + "x" + Unicode.RCEIL, "ceil(x)");

	}

	@Test
	public void testPower() {
		checkCannon("x ^ 2", "x^(2)");
		checkCannon("x ^ 2 + 1", "x^(2)+1");
		checkCannon("x" + Unicode.Superscript_2 + Unicode.Superscript_3,
				"x^(23)");
		checkCannon("x" + Unicode.Superscript_Minus + Unicode.Superscript_2
				+ Unicode.Superscript_3, "x^(-23)");
		checkCannon("1 + x" + Unicode.Superscript_Minus + Unicode.Superscript_2
				+ Unicode.Superscript_3, "1+x^(-23)");
		checkCannon("e^x*sin(x)", "e^(x)*sin(x)");
		checkCannon("e^(-10/x)*sin(x)", "e^(-(10)/(x))*sin(x)");

	}

	@Test
	public void testSubscript() {
		checkCannon("x_2", "x_{2}");
		checkCannon("x_2 = 7", "x_{2}=7");
		checkCannon("x_2 t", "x_{2}*t");
		checkCannon("x_2 sin(x)", "x_{2}*sin(x)");
		checkCannon("f_2(x)", "f_{2}(x)");
		checkCannon("f_2 (x)", "f_{2} (x)");

	}

	@Test
	public void testPoint() {
		checkCannon("(1, 2)", "(1,2)");
		checkCannon("(1; 2)", "(1;2)");
		checkCannon("(1, 2, 3)", "(1,2,3)");
		checkCannon("(1; 2; 3)", "(1;2;3)");
	}

	@Test
	public void testMultiply() {
		checkCannon("t (1,2)", "t (1,2)");
		checkCannon("x x x", "x*x*x");
	}

	@Test
	public void testCommand() {
		checkCannon("turtle1=Turtle[]", "turtle1=Turtle[]");
		checkCannon("Turtle[]", "Turtle[]");
		checkCannon("Turtle[1*3,7]", "Turtle[1*3,7]");
	}

	@Test
	public void testMatrix() {
 		checkCannon("{{1,2},{3,4}}", "{{1,2},{3,4}}");
		checkCannon("{{1 , 2} , { 3 , 4}}", "{{1,2},{3,4}}");
		checkCannon("{{1 , 2} , 3}", "{{1,2},3}");
		checkCannon("{{1,2},{3,4}}+1", "{{1,2},{3,4}}+1");
		checkCannon("{7,{{1,2},{3,4}}+2,4,5,6}", "{7,{{1,2},{3,4}}+2,4,5,6}");
	}

	@Test
	public void testList() {
		checkCannon("{x,1}", "{x,1}");
		checkCannon("{x, 1}", "{x,1}");
		checkCannon("{x ,1}", "{x,1}");
		checkCannon("{x , 1}", "{x,1}");

	}

	@Test
	public void testComma() {
		checkCannon("If[x<1/x,x/2,sqrt(x/2)]",
				"If[x<(1)/(x),(x)/(2),sqrt((x)/(2))]");
		checkCannon("(1;sqrt(2))", "(1;sqrt(2))");
		checkCannon("(t^n;t)", "(t^(n);t)");
	}

	@Test
	public void testParseLaTeX() {
		checkLaTeX("\\frac{x+y}{x-y}", "(x+y)/(x-y)");
		checkLaTeX("\\sqrt{x+y}", "sqrt(x+y)");
		checkLaTeX("\\sqrt{x}+2", "sqrt(x)+2");
		checkLaTeX("1-\\sqrt[3]{x}", "1-nroot(x,3)");
		checkLaTeX("X=\\left(x_0+2x_x,y_0+2x_y\\right)",
				"X=(x_0+2x_x,y_0+2x_y)");
		checkLaTeX("i=\\left[0,\\frac{6\\pi}{p}...24\\pi\\right]",
				"i=[0,(6pi)/(p)...24pi]");
		checkLaTeX(
				"\\left(\\left(1-t\\right)\\left(x_1\\right)+t\\left(x_1+R\\ f\\left(j\\right)\\right),\\left(1-t\\right)\\left(y_1\\right)+t\\left(y_1+Rg\\left(j\\right)\\right)\\right)",
				"((1-t)(x_1)+t(x_1+R f(j)),(1-t)(y_1)+t(y_1+Rg(j)))");
		checkLaTeX("\\frac{x^2}{m^2}+\\frac{y^2}{n^2}\\ge2",
				"(x^(2))/(m^(2))+(y^(2))/(n^(2))>=2");
		checkLaTeX("a\\leq b", "a<=b");
		checkLaTeX("f\\left(x\\right)=\\sin\\left(x\\right)", "f(x)=sin(x)");
		checkLaTeX("r\\ =\\ g^{\\theta}", "r = g^(" + Unicode.thetaStr + ")");
		checkLaTeX("7\\cdot 6", "7*6");
		checkLaTeX("7\\times 6", "7*6");
	}

	@Test
	public void testParseLaTeXAdapter() {
		checkLaTeX("a=\\left[1,...,4\\right]", "a=(1...4)",
				new BracketsAdapter());
		checkLaTeX("a=\\left[0.8,1.2,...,4\\right]",
				"a=Sequence[0.8,4,1.2-(0.8)]", new BracketsAdapter());
	}

	@Test
	public void testKorean() {
		testEditor("\u314D\u3157\u314E", "MathSequence[\uD407]");
		testEditor("\u3141\u3163\u3142\u315C\u3134",
				"MathSequence[\uBBF8, \uBD84]");
		testEditor("\u3145\u3145", "MathSequence[\u110A]");
		testEditor("\u3147\u315C\u3163", "MathSequence[\uC704]");
		testEditor("\u3131\u314F\u3142\u3145", "MathSequence[\uAC13]");
		testEditor(Korean.flattenKorean("\uB098"), "MathSequence[\uB098]");
		testEditor(Korean.flattenKorean("\uB108"), "MathSequence[\uB108]");
		testEditor(Korean.flattenKorean("\uC6B0\uB9AC"),
				"MathSequence[\uC6B0, \uB9AC]");
		testEditor(Korean.flattenKorean("\uBBF8\uBD84"),
				"MathSequence[\uBBF8, \uBD84]");
		testEditor(Korean.flattenKorean("\uBCA1\uD130"),
				"MathSequence[\uBCA1, \uD130]");
		testEditor(Korean.flattenKorean("\uC0C1\uC218"),
				"MathSequence[\uC0C1, \uC218]");
		testEditor(Korean.flattenKorean("\uB2ED\uBA39\uC5B4"),
				"MathSequence[\uB2ED, \uBA39, \uC5B4]");
		testEditor(Korean.flattenKorean("\uC6EC\uC77C"),
				"MathSequence[\uC6EC, \uC77C]");
		testEditor(Korean.flattenKorean("\uC801\uBD84"),
				"MathSequence[\uC801, \uBD84]");
		testEditor(Korean.flattenKorean("\uC288\uD37C\uB9E8"),
				"MathSequence[\uC288, \uD37C, \uB9E8]");
		testEditor(Korean.flattenKorean("\u3137\u3137"),
				"MathSequence[\u1104]");
		testEditor(Korean.flattenKorean("\uC778\uD14C\uADF8\uB784"),
				"MathSequence[\uC778, \uD14C, \uADF8, \uB784]");
		testEditor(Korean.flattenKorean("\u3137"), "MathSequence[\u1103]");
		testEditor(Korean.flattenKorean("\u3131"), "MathSequence[\u1100]");
		testEditor(Korean.flattenKorean("\u3134"), "MathSequence[\u1102]");
		testEditor(Korean.flattenKorean("\uC8FC\uC778\uC7A5"),
				"MathSequence[\uC8FC, \uC778, \uC7A5]");
		testEditor(Korean.flattenKorean("\uC774\uC81C\uC880\uC790\uC790"),
				"MathSequence[\uC774, \uC81C, \uC880, \uC790, \uC790]");
		testEditor(Korean.flattenKorean("\uC544\uBAA8\uB974\uACA0\uB2E4"),
				"MathSequence[\uC544, \uBAA8, \uB974, \uACA0, \uB2E4]");
		
		
	}

	@Test
	public void testEditor() {
		testEditor("sqrt(x/2)",
				"MathSequence[MathFunction[MathSequence[x, /, 2]]]");
	}

	public void testEditor(String input, String output) {
		final MathFieldD mathField = new MathFieldD();

		MathFieldInternal mathFieldInternal = mathField.getInternal();
		InputController inputController = mathFieldInternal
				.getInputController();
		EditorState editorState = mathFieldInternal.getEditorState();

		mathField.insertString(input);

		Assert.assertEquals(output, editorState.getRootComponent().toString());

	}

	private void checkLaTeX(String string, String string2) {
		checkLaTeX(string, string2, null);

	}

	private void checkLaTeX(String string, String string2, BracketsAdapter ad) {
		TeXFormula tf = new TeXFormula();
		TeXParser tp = new TeXParser(string, tf);
		tp.parse();
		Assert.assertEquals(string2,
				new TeXAtomSerializer(ad).serialize(tf.root));
	}

	private static void checkCannon(String input, String output) {
		MathFormula mf = null;
		try {
			mf = parser.parse(input);
			checkLaTeXRender(mf);
		} catch (ParseException e) {
			Assert.assertNull(e);
		}
		Assert.assertNotNull(mf);
		Assert.assertEquals(mf.getRootComponent() + "", output,
				serializer.serialize(mf));
		try {
			mf = parser.parse(output);
			checkLaTeXRender(mf);
		} catch (ParseException e) {
			Assert.assertNull(e);
		}
		
	}

	private static void checkLaTeXRender(MathFormula mf) {
		String tex = TeXSerializer.serialize(mf.getRootComponent(),
				new MetaModel());
		TeXFormula tf = new TeXFormula();
		TeXParser tp = new TeXParser(tex, tf);
		tp.parse();

	}

}
