package org.geogebra.io.latex;

import org.geogebra.common.io.latex.GeoGebraSerializer;
import org.geogebra.common.io.latex.ParseException;
import org.geogebra.common.io.latex.Parser;
import org.geogebra.common.util.Unicode;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.renderer.desktop.FactoryProviderDesktop;
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
	public void testDiv() {
		checkCannon("1/2", "(1)/(2)");
		checkCannon("1/2+3", "(1)/(2)+3");
		checkCannon("1/ ( 2)", "(1)/(2)");
		checkCannon("1/ ( 2+3)", "(1)/(2+3)");
		checkCannon("1/ ((2+3)+4)", "(1)/((2+3)+4)");
		checkCannon("1/(2/3)", "(1)/((2)/(3))");
		
	}

	@Test
	public void testExponent() {
		checkCannon("exp(-30)", "exp(-30)");
		checkCannon(Unicode.EULER_STRING + "^-30", Unicode.EULER_STRING
				+ "^(-30)");
		checkCannon(Unicode.EULER_STRING + "^-30+1", Unicode.EULER_STRING
				+ "^(-30)+1");
		checkCannon(Unicode.EULER_STRING + Unicode.Superscript_Minus
				+ Unicode.Superscript_1
 + Unicode.Superscript_0,
				Unicode.EULER_STRING + "^(-10)");

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
		checkCannon("{{1,2},{3,4}}+1", "{{1,2},{3,4}}+1");
		checkCannon("{7,{{1,2},{3,4}}+2,4,5,6}", "{7,{{1,2},{3,4}}+2,4,5,6}");
	}

	@Test
	public void testComma() {
		checkCannon("If[x<1/x,x/2,sqrt(x/2)]",
				"If[x<(1)/(x),(x)/(2),sqrt((x)/(2))]");
		checkCannon("(1;sqrt(2))", "(1;sqrt(2))");
	}

	private static void checkCannon(String input, String output) {
		MathFormula mf = null;
		try {
			mf = parser.parse(input);
		} catch (ParseException e) {
			Assert.assertNull(e);
		}
		Assert.assertNotNull(mf);
		Assert.assertEquals(mf.getRootComponent() + "", output,
				serializer.serialize(mf));
		
	}

}
