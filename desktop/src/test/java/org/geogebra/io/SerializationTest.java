package org.geogebra.io;

import java.util.Locale;

import org.geogebra.commands.TestErrorHandler;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.StringUtil;
import org.geogebra.desktop.headless.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gwt.regexp.shared.RegExp;
import com.himamis.retex.editor.share.util.Unicode;

public class SerializationTest {
	static AppDNoGui app;

	@BeforeClass
	public static void initialize() {
		app = new AppDNoGui(new LocalizationD(3), true);
	}

	@Test
	public void testSerializationSpeed() {
		app.setLanguage(Locale.US);
		long l = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder(1000);
		FunctionVariable fv = new FunctionVariable(app.getKernel());
		ExpressionNode n = fv.wrap().plus(fv).plus(fv).plus(fv).plus(fv)
				.plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv)
				.plus(fv);
		for (int i = 0; i < 100000; i++) {
			sb.append(n.toValueString(StringTemplate.defaultTemplate));
		}
		System.out.println(System.currentTimeMillis() - l);

		l = System.currentTimeMillis();
		StringBuilder sbm = new StringBuilder(1000);
		ExpressionNode nm = fv.wrap().subtract(fv).subtract(fv).subtract(fv)
				.subtract(fv).subtract(fv).subtract(fv).subtract(fv)
				.subtract(fv).subtract(fv).subtract(fv).subtract(fv)
				.subtract(fv);
		for (int i = 0; i < 100000; i++) {
			sbm.append(nm.toValueString(StringTemplate.defaultTemplate));
		}
		System.out.println(System.currentTimeMillis() - l);
	}

	@Test
	public void testCannonicNumber() {
		Assert.assertEquals("0", StringUtil.cannonicNumber("0.0"));
		Assert.assertEquals("0", StringUtil.cannonicNumber(".0"));
		Assert.assertEquals("1.0E2", StringUtil.cannonicNumber("1.0E2"));
		Assert.assertEquals("1", StringUtil.cannonicNumber("1.00"));
	}

	@Test
	public void testScreenReader() {
		tsc("x^2+2x-1", "x squared plus 2 times x minus 1");
		tsc("sqrt(x+1)", "start square root x plus 1 end square root");
		tsc("(x+1)/(x-1)",
				"start fraction x plus 1 over x minus 1 end fraction");
		tsc("sin(2x)", "sin open parenthesis 2 times x close parenthesis");
	}

	@Test
	public void testScreenReaderFraction() {
		tsc("1/2", "0.5");
		tsc("1+1/2", "start fraction 3 over 2 end fraction");
	}

	@Test
	public void testLaTeX() {
		tex("Mean(1,2)", "mean\\left(1, 2 \\right)");
		tex("Mean({1,2})", "mean\\left(\\left\\{1, 2\\right\\} \\right)");
		tex("6*(4+3)", "6 \\; \\left(4 + 3 \\right)");
	}

	@Test
	public void testConditionalLatex() {
		String caseSimple = "x, \\;\\;\\;\\; \\left(x > 0 \\right)";
		tcl("If[x>0,x]", caseSimple);
		tcl("If[x>0,x,-x]",
				"\\left\\{\\begin{array}{ll} x& : x > 0\\\\ -x& : \\text{otherwise} \\end{array}\\right. ");
		String caseThree = "\\left\\{\\begin{array}{ll} x& : x > 1\\\\ -x& : x < 0\\\\ 7& : \\text{otherwise} \\end{array}\\right. ";
		tcl("If[x>1,x,If[x<0,-x,7]]", caseThree);
		tcl("If[x>1,x,x<0,-x,7]", caseThree);
		String caseTwo = "\\left\\{\\begin{array}{ll} x& : x > 1\\\\ -x& : x <= 0 \\end{array}\\right. ";
		tcl("If[x>1,x,If[x<=0,-x]]", caseTwo);
		tcl("If[x>1,x,x<=0,-x]", caseTwo);
		// x>2 is impossible for x<=0
		tcl("If[x>0,x,If[x>2,-x]]", caseSimple);
		String caseImpossible = "\\left\\{\\begin{array}{ll} x& : x > 1\\\\ -x& : x <= 1 \\end{array}\\right. ";
		// x>1 and x<=2 cover the whole axis, further conditions are irrelevant
		tcl("If[x>1,x,If[x<=2,-x,If[x>3,x^2,x^3]]]", caseImpossible);
		tcl("If[x>1,x,If[x<=2,-x]]", caseImpossible);
	}

	private static void tcl(String string, String string2) {
		GeoElementND geo = eval(string);
		Assert.assertTrue(geo instanceof GeoFunction);
		Assert.assertEquals(
				((GeoFunction) geo).conditionalLaTeX(false,
						StringTemplate.latexTemplate),
				string2.replace("<=", Unicode.LESS_EQUAL + ""));
	}

	private static void tsc(String string, String string2) {
		GeoElementND geo = eval(string);
		Assert.assertEquals(string2,
				geo.toValueString(StringTemplate.screenReader).trim()
						.replaceAll(" +", " "));
	}

	private static void tex(String string, String string2) {
		GeoElementND geo = eval(string);
		Assert.assertEquals(string2,
				geo.getDefinition(StringTemplate.latexTemplate));
	}

	private static GeoElementND eval(String string) {
		AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
		GeoElementND[] result = ap.processAlgebraCommandNoExceptionHandling(
				string, false, new TestErrorHandler(),
				new EvalInfo(true).withFractions(true), null);
		return result[0];
	}

	@Test
	public void testInequality() {
		String[] testI = new String[] { "(x>=3) && (7>=x) && (10>=x)" };
		String[] test = new String[] { "aaa", "(a)+b", "3", "((a)+(b))+7" };
		String[] testFalse = new String[] { "3(", "(((7)))" };
		for (String t : test) {
			Assert.assertTrue(
					RegExp.compile("^" + CASgiac.expression + "$").test(t));

		}
		for (String t : testFalse) {
			Assert.assertFalse(
					RegExp.compile("^" + CASgiac.expression + "$").test(t));

		}
		for (String t : testI) {
			Assert.assertTrue(CASgiac.inequality.test(t));
		}
	}

}
