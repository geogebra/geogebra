package org.geogebra.common.io;

import static org.geogebra.test.TestStringUtil.unicode;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.AppCommon3D;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.regexp.shared.RegExp;
import org.geogebra.test.OrderingComparison;
import org.geogebra.test.TestErrorHandler;
import org.junit.Before;
import org.junit.Test;

import com.himamis.retex.editor.share.util.Unicode;

public class StringTemplateTest {
	private AppCommon3D app;

	@Before
	public void initialize() {
		app = AppCommonFactory.create3D();
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
		assertThat(System.currentTimeMillis() - l,
				OrderingComparison.lessThan(10000L));
	}

	@Test
	public void testCanonicalNumber() {
		assertEquals("0", StringUtil.canonicalNumber("0.0"));
		assertEquals("0", StringUtil.canonicalNumber(".0"));
		assertEquals("1.0E2", StringUtil.canonicalNumber("1.0E2"));
		assertEquals("1", StringUtil.canonicalNumber("1.00"));
	}

	@Test
	public void testLaTeX() {
		tex("Mean(1,2)", "mean\\left(1, 2 \\right)");
		tex("Mean({1,2})", "mean\\left(\\left\\{1,\\;2\\right\\} \\right)");
		tex("6*(4+3)", "6 \\; \\left(4 + 3 \\right)");
		tex("69%", "69\\%");
		tex("4*20%", "4 \\cdot 20\\%");
		tex("13.37%", "13.37\\%");
		tex("13*3.7%", "13 \\cdot 3.7\\%");
		tex("(1,2)", "\\left(1,\\;2 \\right)");
		tex("(1,2,3)", "\\left(1,\\;2,\\;3 \\right)");
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
		assertEquals(string2, geo.getDefinitionForInputBar());
	}

	@Test
	public void testConditionalLatex() {
		String caseSimple = "x, \\;\\;\\;\\; \\left(x > 0 \\right)";
		tcl("If[x>0,x]", caseSimple);
		tcl("If[x>0,x,-x]", "\\left\\{\\begin{array}{ll} x& : x > 0\\\\"
				+ " -x& : \\text{otherwise} \\end{array}\\right. ");
		String caseThree = "\\left\\{\\begin{array}{ll} x& : x > 1\\\\"
				+ " -x& : x < 0\\\\ 7& : \\text{otherwise} \\end{array}\\right. ";
		tcl("If[x>1,x,If[x<0,-x,7]]", caseThree);
		tcl("If[x>1,x,x<0,-x,7]", caseThree);
		String caseTwo = "\\left\\{\\begin{array}{ll} x& : x > 1\\\\"
				+ " -x& : x <= 0 \\end{array}\\right. ";
		tcl("If[x>1,x,If[x<=0,-x]]", caseTwo);
		tcl("If[x>1,x,x<=0,-x]", caseTwo);
		// x>2 is impossible for x<=0
		tcl("If[x>0,x,If[x>2,-x]]", caseSimple);
		String caseImpossible = "\\left\\{\\begin{array}{ll} x& : x > 1\\\\"
				+ " -x& : x <= 1 \\end{array}\\right. ";
		// x>1 and x<=2 cover the whole axis, further conditions are irrelevant
		tcl("If[x>1,x,If[x<=2,-x,If[x>3,x^2,x^3]]]", caseImpossible);
		tcl("If[x>1,x,If[x<=2,-x]]", caseImpossible);
	}

	private void tcl(String command, String expected) {
		GeoElementND geo = add(command);
		assertThat(geo, instanceOf(GeoFunction.class));
		assertEquals(
				expected.replace("<=", Unicode.LESS_EQUAL + ""),
				((GeoFunction) geo).conditionalLaTeX(false,
						StringTemplate.latexTemplate));
	}

	private void tex(String string, String string2) {
		GeoElementND geo = add(string);
		assertEquals(string2,
				geo.getDefinition(StringTemplate.latexTemplate));
	}

	private GeoElementND add(String string) {
		AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
		GeoElementND[] result = ap.processAlgebraCommandNoExceptionHandling(
				string, false, TestErrorHandler.INSTANCE,
				new EvalInfo(true).withSymbolic(true).addDegree(true), null);
		return result[0];
	}

	@Test
	public void editorTemplateShouldRetainPrecision() {
		GeoElementND f = add("f:0.33333x");
		assertEquals("f(x)=0.33333 x",
				f.toString(StringTemplate.editorTemplate));
		assertEquals("f(x) = 0.33333x",
				f.toString(StringTemplate.editTemplate));
		assertEquals("f(x) = 0.33x",
				f.toString(StringTemplate.defaultTemplate));
	}

	@Test
	public void testInequality() {
		String[] testI = new String[]{"(x>=3) && (7>=x) && (10>=x)"};
		String[] test = new String[]{"aaa", "(a)+b", "3", "((a)+(b))+7"};
		String[] testFalse = new String[]{"3(", "(((7)))"};
		for (String t : test) {
			assertTrue(
					RegExp.compile("^" + CASgiac.expression + "$").test(t));

		}
		for (String t : testFalse) {
			assertFalse(
					RegExp.compile("^" + CASgiac.expression + "$").test(t));

		}
		for (String t : testI) {
			assertTrue(CASgiac.inequality.test(t));
		}
	}

	@Test
	public void shouldUseTrigPowerForConstantExponent() {
		FunctionVariable x = new FunctionVariable(app.getKernel());
		ExpressionNode node = x.wrap().sin().power(2);
		assertEquals("sin" + Unicode.SUPERSCRIPT_2 + "(x)",
				node.toString(StringTemplate.editTemplate));
		assertEquals("\\operatorname{sin} ^{2}\\left( x \\right)",
				node.toString(StringTemplate.latexTemplate));
	}

	@Test
	public void shouldUseTrigPowerForVarExponent() {
		FunctionVariable x = new FunctionVariable(app.getKernel());
		ExpressionNode node = x.wrap().sin().power(x.wrap().cos());
		assertEquals("(sin(x))^cos(x)",
				node.toString(StringTemplate.editTemplate));
		assertEquals("\\left(\\operatorname{sin} \\left( x \\right) \\right)"
						+ "^{\\operatorname{cos} \\left( x \\right)}",
				node.toString(StringTemplate.latexTemplate));
	}

	@Test
	public void shouldUseBracketsForFunctionPowers() {
		ExpressionNode node = functionPower(Operation.LOG, 2);
		assertEquals(unicode("(ln(x))^2"),
				node.toString(StringTemplate.editTemplate));
		node = functionPower(Operation.ARCSIN, 3);
		assertEquals(unicode("(sin^-1(x))^3"),
				node.toString(StringTemplate.editTemplate));
	}

	private ExpressionNode functionPower(Operation op, int exponent) {
		FunctionVariable x = new FunctionVariable(app.getKernel());
		return x.wrap().apply(op).power(new MyDouble(app.getKernel(), exponent));
	}

	@Test
	public void testConvertScientificNotationGiac() {
		StringTemplate template = StringTemplate.giacTemplate;
		assertThat(template.convertScientificNotationGiac("3E3"), is("3000"));
		assertThat(template.convertScientificNotationGiac("3.33"), is("(333/100)"));
		assertThat(template.convertScientificNotationGiac("3.33E1"), is("(333/10)"));
		assertThat(template.convertScientificNotationGiac("3.33E2"), is("333"));
		assertThat(template.convertScientificNotationGiac("3.33E3"), is("3330"));
	}

	@Test
	public void definitionShouldKeepSmallNumbers() {
		GeoElementND num = add("a=1E-20");
		assertEquals("1*10^(-20)",
				num.getDefinition(StringTemplate.editTemplate));
	}

	@Test
	public void definitionShouldKeepSmallNumbersScientific() {
		GeoElementND num = add("a=1E-20");
		StringTemplate latexNoLocal = StringTemplate.defaultTemplate
				.derivePrecisionPreservingLaTeXTemplate();
		latexNoLocal.setLocalizeCmds(false);
		assertEquals("1 \\cdot 10^{-20}", num.getDefinition(latexNoLocal));
	}

	@Test
	public void powerWithScientificNotationShouldHaveBrackets() {
		GeoElementND fn = add("x^(3E-20)");
		assertEquals("f(x) = x^(3*10^(-20))",
				fn.toString(StringTemplate.editTemplate));
		GeoElementND fn2 = add("3E20^x");
		assertEquals("g(x) = (3*10^(20))^x",
				fn2.toString(StringTemplate.editTemplate));
		GeoElementND fn3 = add("x^3E20");
		assertEquals("h(x) = x^(3*10^(20))",
				fn3.toString(StringTemplate.editTemplate));
		GeoElementND num = add("3E-20!");
		assertEquals("(3*10^(-20))!",
				num.getDefinition(StringTemplate.editTemplate));
	}

	@Test
	public void factorialWithScientificNotationShouldHaveBrackets() {
		GeoElementND num = add("3E-20!");
		assertEquals("(3*10^(-20))!",
				num.getDefinition(StringTemplate.editTemplate));
	}

	@Test
	public void fractionWithScientificNotationShouldHaveBrackets() {
		GeoElementND num = add("1/3E-20");
		assertEquals("1 / (3*10^(-20))",
				num.getDefinition(StringTemplate.editTemplate));
	}

	@Test
	public void vectorMultiplicationShouldUseBrackets() {
		add("u = Vector((1,2), (2,3))");
		add("v = Vector((3,1), (5,2))");
		add("w = Vector((0,2), (2,4))");
		plain("a = u * (v * w)", "a = u (v w)");
		plain("b = (u * v) * w", "b = (u v) w");
		plain("c = v * v * 3", "c = (v v) * 3");
	}

	@Test
	public void matrixVectorMultiplicationShouldUseBrackets() {
		add("u = Vector((1,2), (2,3))");
		add("v = Vector((3,1), (5,2))");
		add("M = {{1,3},{2,4}}");
		plain("a = M * (v * u)", "a = M (v u)");
		plain("b = v * (u * M)", "b = v (u M)");
	}

	@Test
	public void vectorMultiplicationShouldNotUseBrackets() {
		add("u = Vector((1,2), (2,3))");
		add("v = Vector((3,1), (5,2))");
		plain("a = u * v", "a = u v");
		plain("b = u * 3", "b = u * 3");
	}

	@Test
	public void matrixMultiplicationShouldNotUseBrackets() {
		add("M = {{1,3},{2,4}}");
		add("N = {{0,-1},{1,0}}");
		plain("m1 = (N * M) * M", "m1 = N M M");
		plain("m2 = M * M * N", "m2 = M M N");
	}
}
