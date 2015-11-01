package org.geogebra.commands;

import java.util.Locale;

import javax.swing.JFrame;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.Unicode;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.main.AppD;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommandsTest extends Assert{
	static AppD app;
	static AlgebraProcessor ap;

	private static void  t(String input, String expected){
		testSyntax(input, new String[] { expected }, app, ap,
				StringTemplate.xmlTemplate);
	}

	private static void t(String input, String expected, StringTemplate tpl) {
		testSyntax(input, new String[] { expected }, app, ap, tpl);
	}

	public static void t(String s, String[] expected, StringTemplate tpl) {
		testSyntax(s, expected, app, ap, tpl);
	}

	public static void t(String s, String[] expected) {
		testSyntax(s, expected, app, ap, StringTemplate.xmlTemplate);
	}

	public static void testSyntax(String s, String[] expected, App app,
			AlgebraProcessor ap, StringTemplate tpl) {
		if(syntaxes==-1000){
			Throwable t = new Throwable();
			String cmdName = t.getStackTrace()[2].getMethodName().substring(3);
			String syntax = app.getLocalization().getCommand(cmdName+".Syntax");
			syntaxes = 0;
			for(int i=0;i<syntax.length();i++)
				if(syntax.charAt(i)=='[')syntaxes++;
			System.out.println();
			System.out.print(cmdName+" ");
			
			/*
			// This code helps to force timeout for each syntax. Not used at the moment.
			GeoGebraCAS cas = (GeoGebraCAS) app.getKernel()
					.getGeoGebraCAS();
			try {
				cas.getCurrentCAS().evaluateRaw("caseval(\"timeout 8\")");
			} catch (Throwable e) {
				App.error("CAS error " + e);
			} 
			*/
			
			
		}
		Throwable t = null;
		GeoElement[] result = null;
		try {
			result = ap.processAlgebraCommandNoExceptionHandling(s,
					false, false, true, false);
		}catch (Throwable e) {
			t = e;
		}
		syntaxes--;
		assertNull(t);
		Assert.assertNotNull(s,result);

		Assert.assertEquals(s + " count:", expected.length, result.length);
		for (int i = 0; i < expected.length; i++) {
			String actual = result[i].toValueString(tpl);
			Assert.assertEquals(s + ":" + actual, expected[i], actual);
		}
		System.out.print("+");

	}

	private static int syntaxes = -1000;
	
	@Before
	public void resetSyntaxes(){
		syntaxes = -1000;
	}
	@After
	public void checkSyntaxes(){
		Assert.assertTrue("unchecked syntaxes: "+syntaxes,syntaxes<=0);
	}
	
	@BeforeClass
	public static void setupApp() {
		app = new App3D(new CommandLineArguments(
				new String[] {
				"--prerelease" }), new JFrame(), false);
		app.setLanguage(Locale.US);
		ap = app.getKernel().getAlgebraProcessor();
	    // Setting the general timeout to 11 seconds. Feel free to change this.
		app.getKernel().getApplication().getSettings().getCasSettings().setTimeoutMilliseconds(11000);
	}

	@Test
	public void testQuadricExpr() {
		t("-y^2=z-1", "-y" + Unicode.Superscript_2 + " + 0z"
				+ Unicode.Superscript_2 + " - z = -1");
		t("y^2=1-z", "y" + Unicode.Superscript_2 + " + 0z"
				+ Unicode.Superscript_2 + " + z = 1");
	}

	@Test
	public void listArithmetic() {
		t("{1,2,3}*2", "{2, 4, 6}");
		t("{1,2,3}+3", "{4, 5, 6}");
		t("list1:={1,2,3}", "{1, 2, 3}");
		t("listF:={x, 2 * x,3 * x+1}", "{x, (2 * x), (3 * x) + 1}");
		t("matrix1:={{1, 2, 3}, {2, 4, 6}, {3, 6, 9}}",
				"{{1, 2, 3}, {2, 4, 6}, {3, 6, 9}}");
		t("list1(1)", "1");
		t("list1(4)", "NaN");
		t("list1(0)", "NaN");
		t("list1(-1)", "3");
		t("list1(-5)", "NaN");
		t("list1(1,2)", "NaN");
		t("listF(1)", "x");
		t("listF(2)", "(2 * x)");
		t("listF(2,7)", "14");
		t("matrix1(2)", "{2, 4, 6}");
		t("matrix1(-1)", "{3, 6, 9}");
		t("matrix1(-5)", "{NaN, NaN, NaN}");
		t("matrix1(2,3)", "6");
		t("matrix1(2,3,4)", "NaN");
		t("matrix1(2,-1)", "6");
		t("Delete[list1]", new String[] {});
		t("Delete[matrix1]", new String[] {});
	}

	@Test
	public void operationSequence() {
		Assert.assertEquals(StringUtil.fixVerticalBars("1..2"), "1"
				+ Unicode.ellipsis + "2");
		t("3.2..7.999", "{3, 4, 5, 6, 7, 8}");
		t("-3.2..3.2", "{-3, -2, -1, 0, 1, 2, 3}");
		t("3.2..-2", "{3, 2, 1, 0, -1, -2}");
	}
	@Test
	public void cmdMidpoint(){
		t("Midpoint[(0,0),(2,2)]","(1, 1)");
		t("Midpoint[0<x<2]","1");
		t("Midpoint[Segment[(0,0),(2,2)]]","(1, 1)");
		t("Midpoint[(x-1)^2+(y-1)^2=pi]","(1, 1)");
	}
	
	@Test
	public void cmdIsInRegion(){
		t("IsInRegion[(0,0),Circle[(1,1),2]]","true");
		t("IsInRegion[(0,0),Circle[(1,1),1]]","false");
		t("IsInRegion[(0,0,0),x+y+z=1]","false");
		t("IsInRegion[(0,0,0),x+y+z=0]","true");
		t("IsInRegion[(0,0,0),Polygon[(0,0,1),(1,0,0),(0,1,0)]]","false");
		t("IsInRegion[(1/3,1/3,1/3),Polygon[(0,0,1),(1,0,0),(0,1,0)]]","true");
		//move the centroid a bit in z-axis, it should no longer be inside
		t("IsInRegion[(1/3,1/3,1/2),Polygon[(0,0,1),(1,0,0),(0,1,0)]]","false");
	}
	
	@Test
	public void cmdCross(){
		t("Cross[(0,0,1),(1,0,0)]","(0, 1, 0)");
		t("Cross[(0,0,1),(0,1,0)]","(-1, 0, 0)");
		t("Cross[(0,0,1),(0,0,1)]","(0, 0, 0)");
		t("Cross[(0,1),(2,0)]","-2");
		t("Cross[(0,1),(0,2)]","0");
	}
	
	@Test
	public void cmdDot(){
		t("Dot[(0,0,1),(1,0,0)]","0");
		t("Dot[(0,0,1),(0,0,1)]","1");
		t("Dot[(0,3),(0,2)]","6");
	}

	@Test
	public void cmdNormalize() {
		t("Normalize[{1,3,2}]", "{0, 1, 0.5}");
		t("Normalize[{(1,1),(3,1),(2,1)}]", "{(0, 0), (1, 0), (0.5, 0)}");
	}
	
	@Test
	public void cmdDataFunction(){
		t("DataFunction[]", "DataFunction[{}, {},x]");
		t("DataFunction[]", new String[] { "DataFunction[x]" },
				StringTemplate.defaultTemplate);
	}
	
	@Test
	public void cmdAreCongruent() {
		t("AreCongruent[Segment[(0,1),(1,0)],Segment[(1,0),(0,1)]]", "true");
		t("AreCongruent[Segment[(0,1),(1,0)],Segment[(-1,0),(0,-1)]]", "true");
		t("AreCongruent[Segment[(0,1),(1,0)],Segment[(2,0),(0,2)]]", "false");
	}

	@Test
	public void cmdIntersect() {
		t("Intersect[3x=4y,Curve[5*sin(t),5*cos(t),t,0,6]]", new String[] {
				"(4, 3)", "(-4, -3)" },
				StringTemplate.editTemplate);
		t("Intersect[x=y,x+y=2]", "(1, 1)");
		t("Intersect[x=y,x^2+y^2=2]", new String[] { "(1, 1)", "(-1, -1)" });
		t("Intersect[x=y,x^2+y^2=2, 1]", "(1, 1)");
		t("Intersect[x=y,x^2+y^2=2, (-5, -3)]", "(-1, -1)");
	}

	@Test
	public void cmdNumerator() {
		t("Numerator[ (x + 2)/(x+1) ]", "x + 2");
		t("Numerator[ 3/7 ]", "3");
		t("Numerator[ 5/(-8) ]", "-5");
		t("Numerator[ 2/0 ]", "1");
	}

	@Test
	public void cmdDenominator() {
		t("Denominator[ (x + 2)/(x+1) ]", "x + 1");
		t("Denominator[ 3/7 ]", "7");
		t("Denominator[ 5/(-8) ]", "8");
		t("Denominator[ 2/0 ]", "0");
	}

	@Test
	public void cmdMaximize() {
		t("slider:=Slider[0,5]", "0");
		t("Maximize[ 5-(3-slider)^2, slider ]", "3");
		t("ptPath:=Point[(x-3)^2+(y-4)^2=25]", "(0, 0)",
				StringTemplate.defaultTemplate);
		t("Maximize[ y(ptPath), ptPath ]", "(3, 9)",
				StringTemplate.defaultTemplate);
	}

	@Test
	public void cmdMinimize() {
		t("slider:=Slider[0,5]", "0");
		t("Minimize[ 5+(3-slider)^2, slider ]", "3");
		t("ptPath:=Point[(x-3)^2+(y-4)^2=25]", "(0, 0)",
				StringTemplate.defaultTemplate);
		t("Minimize[ y(ptPath), ptPath ]", "(3, -1)",
				StringTemplate.defaultTemplate);
	}

	@Test
	public void cmdIteration() {
		t("Iteration[ x*2, 2, 5 ]", "64");
		t("Iteration[ t*2, t, {(2,3)}, 5 ]", "(64, 96)");
	}

	@Test
	public void cmdIterationList() {
		t("IterationList[ x*2, 2, 5 ]", "{2, 4, 8, 16, 32, 64}");
		t("IterationList[ a+b, a, b, {1,1}, 5 ]", "{1, 1, 2, 3, 5, 8}");
	}

	@Test
	public void cmdImplicitSurface() {
		t("ImplicitSurface[sin(x)+sin(y)+sin(z)]",
				"sin(x) + sin(y) + sin(z) = 0");
	}

	@Test
	public void cmdSetConstructionStep() {
		app.setSaved();
		app.clearConstruction();
		t("cs=ConstructionStep[]", "1");
		t("2", "2");
		t("7", "7");
		t("SetConstructionStep[2]", new String[] {});
		t("cs", "2");
		t("SetConstructionStep[1]", new String[] {});
		t("cs", "1");
		app.clearConstruction();
	}

	@Test
	public void cmdSVD() {
		t("SVD[ {{1}} ]", "{{{1}}, {{1}}, {{1}}}");
	}

	@Test
	public void cmdSequence() {
		t("Sequence[ 4 ]", "{1, 2, 3, 4}");
		t("Sequence[ 3.2, 7.999 ]", "{3, 4, 5, 6, 7, 8}");
		t("Sequence[ -3.2, 3.2 ]", "{-3, -2, -1, 0, 1, 2, 3}");
		t("Sequence[ 3.2, -2 ]", "{3, 2, 1, 0, -1, -2}");
		t("Sequence[ t^2, t, 1, 4 ]", "{1, 4, 9, 16}");
		t("Sequence[ t^2, t, 1, 4, 2 ]", "{1, 9}");
		t("Sequence[ t^2, t, 1, 4, -2 ]", "{}");
		t("Length[Unique[Sequence[ random(), t, 1, 10]]]", "10");

	}

	@Test
	public void cmdDifference() {
		t("Difference[Polygon[(0,0),(2,0),4],Polygon[(1,1),(3,1),(3,3),(1,3)]]",
				new String[] { "3", "(2, 1)", "(1, 1)", "(1, 2)", "(0, 2)",
						"(0, 0)", "(2, 0)", "1", "1", "1", "2", "2", "1" },
				StringTemplate.defaultTemplate);
		t("Difference[Polygon[(0,0),(2,0),4],Polygon[(1,1),(3,1),(3,3),(1,3)], true]",
				new String[] { "3", "3", "(3, 3)", "(1, 3)", "(1, 2)",
						"(2, 2)", "(2, 1)", "(3, 1)", "(2, 1)", "(1, 1)",
						"(1, 2)", "(0, 2)", "(0, 0)", "(2, 0)", "2", "1", "1",
						"1", "1", "2", "1", "1", "1", "2", "2", "1" },
				StringTemplate.defaultTemplate);
	}
}
