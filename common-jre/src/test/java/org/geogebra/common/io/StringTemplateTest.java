package org.geogebra.common.io;

import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.factories.AwtFactoryCommon;
import org.geogebra.common.jre.headless.LocalizationCommon;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.util.StringUtil;
import org.geogebra.test.OrderingComparison;
import org.geogebra.test.TestErrorHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gwt.regexp.shared.RegExp;
import com.himamis.retex.editor.share.util.Unicode;

public class StringTemplateTest {
	static AppCommon3D app;

	@BeforeClass
	public static void initialize() {
		app = new AppCommon3D(new LocalizationCommon(3),
				new AwtFactoryCommon());
	}

	@Before
	public void cleanup() {
		app.getKernel().clearConstruction(true);
	}

	@Test
	public void testSerializationSpeed() {
		app.setLanguage("en_US");
		long l = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder(1000);
		FunctionVariable fv = new FunctionVariable(app.getKernel());
		ExpressionNode plusNode = fv.wrap().plus(fv).plus(fv).plus(fv).plus(fv)
				.plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv)
				.plus(fv);
		for (int i = 0; i < 1E4; i++) {
			sb.append(plusNode.toValueString(StringTemplate.defaultTemplate));
		}

		StringBuilder sbm = new StringBuilder(1000);
		ExpressionNode minusNode = fv.wrap().subtract(fv).subtract(fv).subtract(fv)
				.subtract(fv).subtract(fv).subtract(fv).subtract(fv)
				.subtract(fv).subtract(fv).subtract(fv).subtract(fv)
				.subtract(fv);
		for (int i = 0; i < 1E4; i++) {
			sbm.append(minusNode.toValueString(StringTemplate.defaultTemplate));
		}
		Assert.assertThat(System.currentTimeMillis() - l,
				OrderingComparison.lessThan(10000L));
	}

	@Test
	public void testCannonicNumber() {
		Assert.assertEquals("0", StringUtil.cannonicNumber("0.0"));
		Assert.assertEquals("0", StringUtil.cannonicNumber(".0"));
		Assert.assertEquals("1.0E2", StringUtil.cannonicNumber("1.0E2"));
		Assert.assertEquals("1", StringUtil.cannonicNumber("1.00"));
	}

	@Test
	public void testLaTeX() {
		tex("Mean(1,2)", "mean\\left(1, 2 \\right)");
		tex("Mean({1,2})", "mean\\left(\\left\\{1, 2\\right\\} \\right)");
		tex("6*(4+3)", "6 \\; \\left(4 + 3 \\right)");
	}

	@Test
	public void testDegrees() {
		plain("s:sin(8'3'')", "s = sin(8'3" + Unicode.SECONDS + ")");
		plain("c:cos(1" + Unicode.DEGREE_STRING + "8'3'')", "c = cos(1"
				+ Unicode.DEGREE_STRING + "8'3"
				+ Unicode.SECONDS + ")");
	}

	private void plain(String string, String string2) {
		GeoElementND geo = add(string);
		Assert.assertEquals(string2, geo.getDefinitionForInputBar());
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
		GeoElementND geo = add(string);
		Assert.assertTrue(geo instanceof GeoFunction);
		Assert.assertEquals(
				((GeoFunction) geo).conditionalLaTeX(false,
						StringTemplate.latexTemplate),
				string2.replace("<=", Unicode.LESS_EQUAL + ""));
	}

	private static void tex(String string, String string2) {
		GeoElementND geo = add(string);
		Assert.assertEquals(string2,
				geo.getDefinition(StringTemplate.latexTemplate));
	}

	private static GeoElementND add(String string) {
		AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
		GeoElementND[] result = ap.processAlgebraCommandNoExceptionHandling(
				string, false, TestErrorHandler.INSTANCE,
				new EvalInfo(true).withFractions(true).addDegree(true), null);
		return result[0];
	}

	@Test
	public void editorTemplateShouldRetainPrecision() {
		GeoElementND f = add("f:0.33333x");
		Assert.assertEquals("f(x) = 0.33333x",
				f.toString(StringTemplate.editorTemplate));
		Assert.assertEquals("f(x) = 0.33333x",
				f.toString(StringTemplate.editTemplate));
		Assert.assertEquals("f(x) = 0.33x",
				f.toString(StringTemplate.defaultTemplate));
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
