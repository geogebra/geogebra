package org.geogebra.io;
import java.util.Locale;

import org.geogebra.common.cas.giac.CASgiac;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gwt.regexp.shared.RegExp;

public class SerializationTest {
	static AppDNoGui app;

	@BeforeClass
	public static void initialize() {
		app = new AppDNoGui(new LocalizationD(3), true);
	}
	@Test
	public void testSerializationSpeed(){

		app.setLanguage(Locale.US);
		long l = System.currentTimeMillis();
		StringBuilder sb = new StringBuilder(1000);
		FunctionVariable fv = new FunctionVariable(app.getKernel());
		ExpressionNode n = fv.wrap().plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv).plus(fv);
		for(int i = 0;i<100000;i++){
			sb.append(n.toValueString(StringTemplate.defaultTemplate));
		}
		System.out.println(System.currentTimeMillis() - l);
		
		l = System.currentTimeMillis();
		StringBuilder sbm = new StringBuilder(1000);
		ExpressionNode nm = fv.wrap().subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv).subtract(fv);
		for(int i = 0;i<100000;i++){
			sbm.append(nm.toValueString(StringTemplate.defaultTemplate));
		}
		System.out.println(System.currentTimeMillis() - l);
	}
	
	@Test
	public void testCannonicNumber(){
		Assert.assertEquals("0", StringUtil.cannonicNumber("0.0"));
		Assert.assertEquals("0", StringUtil.cannonicNumber(".0"));
		Assert.assertEquals("1.0E2", StringUtil.cannonicNumber("1.0E2"));
		Assert.assertEquals("1", StringUtil.cannonicNumber("1.00"));
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
		AlgebraProcessor ap =app.getKernel().getAlgebraProcessor();
		GeoElementND[] result =  ap.processAlgebraCommand(string, false);
		Assert.assertTrue(result[0] instanceof GeoFunction);
		Assert.assertEquals(((GeoFunction) result[0]).conditionalLaTeX(false,
				StringTemplate.latexTemplate), string2.replace("<=",
				Unicode.LESS_EQUAL + ""));

	}

	@Test
	public void testInequality() {
		String[] testI = new String[] { "(x>=3) && (7>=x) && (10>=x)" };
		String[] test = new String[] { "aaa", "(a)+b", "3", "((a)+(b))+7" };
		String[] testFalse = new String[] { "3(", "(((7)))" };
		for (String t : test) {
			Assert.assertTrue(RegExp.compile("^" + CASgiac.expression + "$")
					.test(t));

		}
		for (String t : testFalse) {
			Assert.assertFalse(RegExp.compile("^" + CASgiac.expression + "$")
					.test(t));

		}
		for (String t : testI) {
			Assert.assertTrue(CASgiac.inequality.test(t));
		}
	}

}
