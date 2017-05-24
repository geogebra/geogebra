package org.geogebra.commands;

import java.util.Locale;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.lang.Unicode;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class CommandsTest extends Assert{
	static AppDNoGui app;
	static AlgebraProcessor ap;
	private static String syntax;

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

	public static void testSyntax(String s, String[] expected, App app1,
			AlgebraProcessor proc, StringTemplate tpl) {
		if(syntaxes==-1000){
			Throwable t = new Throwable();
			String cmdName = t.getStackTrace()[2].getMethodName().substring(3);
			syntax = app1.getLocalization().getCommand(cmdName + ".Syntax");
			syntaxes = 0;
			for (int i = 0; i < syntax.length(); i++) {
				if (syntax.charAt(i) == '[') {
					syntaxes++;
				}
			}
			String syntax3D = app1.getLocalization()
					.getCommand(
					cmdName + ".Syntax3D");
			if (syntax3D.contains("[")) {
				syntax += "\n" + syntax3D;
			}
			for (int i = 0; i < syntax3D.length(); i++) {
				if (syntax3D.charAt(i) == '[') {
					syntaxes++;
				}
			}
			System.out.println();
			System.out.print(cmdName);
			
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
		GeoElementND[] result = null;
		try {
			result = proc.processAlgebraCommandNoExceptionHandling(s,
					false, TestErrorHandler.INSTANCE, false, null);
		}catch (Throwable e) {
			t = e;
		}
		syntaxes--;
		assertNull(t);
		Assert.assertNotNull(s,result);
		// for (int i = 0; i < result.length; i++) {
		// String actual = result[i].toValueString(tpl);
		// System.out.println("\"" + actual + "\",");
		// }
		Assert.assertEquals(s + " count:", expected.length, result.length);

		for (int i = 0; i < expected.length; i++) {
			String actual = result[i].toValueString(tpl);
			Assert.assertEquals(s + ":" + actual, expected[i], actual);
		}
		System.out.print("+");

	}

	public void checkError(String s, String msg) {
		ErrorAccumulator errorStore = new ErrorAccumulator();

			app.getKernel().getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(s, false,
							errorStore, false, null);

		assertTrue(msg.equals(errorStore.getErrors()));

	}

	private static int syntaxes = -1000;
	
	@Before
	public void resetSyntaxes(){
		syntaxes = -1000;
		app.getKernel().clearConstruction(true);
	}
	@After
	public void checkSyntaxes(){
		Assert.assertTrue("unchecked syntaxes: " + syntaxes + syntax,
				syntaxes <= 0);
	}
	
	@BeforeClass
	public static void setupApp() {
		app = new AppDNoGui(new LocalizationD(3), false);
		app.setLanguage(Locale.US);
		ap = app.getKernel().getAlgebraProcessor();
		// make sure x=y is a line, not plane
		app.getGgbApi().setPerspective("1");
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
		t("aa:=1", "1");
		t("matrix2:={{aa}}", "{{1}}");
		// app.getKernel().lookupLabel("matrix2").setFixed(true);
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
		t("matrix1(5,2)", "NaN");
		t("matrix1(2,5)", "NaN");
		t("matrix2(1,2)", "NaN");
		t("matrix2(2,1)", "NaN");
		t("Delete[list1]", new String[] {});
		t("Delete[matrix1]", new String[] {});
	}

	@Test
	public void tuples() {
		t("(1..2,1..2)", "{(1, 1), (2, 2)}");
		t("(1,1..5)", "{(1, 1), (1, 2), (1, 3), (1, 4), (1, 5)}");
		t("(1,1..5,6..2)",
				"{(1, 1, 6), (1, 2, 5), (1, 3, 4), (1, 4, 3), (1, 5, 2)}");
		t("(1;(1..5)*2pi/5)",
				unicode("{(1; 72deg), (1; 144deg), (1; 216deg), (1; 288deg), (1; 0deg)}"),
				StringTemplate.editTemplate);
	}

	@Test
	public void equationListTest() {
		t("x+y=1..5",
				"{x + y = 1, x + y = 2, x + y = 3, x + y = 4, x + y = 5}");
		t("x^2+y^2=1..5",
				unicode("{x^2 + y^2 = 1, x^2 + y^2 = 2, x^2 + y^2 = 3, x^2 + y^2 = 4, x^2 + y^2 = 5}"),
				StringTemplate.editTemplate);
		t("f(r)=(r,sin(r)*(1..5))",
				"{(r, sin(r)), (r, (sin(r) * 2)), (r, (sin(r) * 3)), (r, (sin(r) * 4)), (r, (sin(r) * 5))}");
		t("f(r)=(r+(1..5),sin(r)*(1..5))",
				"{(r + 1, sin(r)), (r + 2, (sin(r) * 2)), (r + 3, (sin(r) * 3)), (r + 4, (sin(r) * 4)), (r + 5, (sin(r) * 5))}");
		t("f(r)=((1..5)*r,sin(r)+1)",
				"{(r, sin(r) + 1), ((2 * r), sin(r) + 1), ((3 * r), sin(r) + 1), ((4 * r), sin(r) + 1), ((5 * r), sin(r) + 1)}");
	}

	@Test
	public void functionsList() {
		t("(1..2)+x*(1..2)", "{1 + x, 2 + (x * 2)}");
		t("list1=(-2..2)", "{-2, -1, 0, 1, 2}");
		t("(list1*t,(1-t)*(1-list1))",
				"{((-2 * t), ((1 - t) * 3)), ((-t), ((1 - t) * 2)), ((0 * t), (1 - t)), (t, ((1 - t) * 0)), ((2 * t), ((1 - t) * (-1)))}");
	}

	private static GeoElement get(String label) {
		return app.getKernel().lookupLabel(label);
	}

	@Test
	public void listPropertiesTest() {
		t("mat1={{1,2,3}}", "{{1, 2, 3}}");
		Assert.assertTrue(((GeoList) get("mat1")).isEditableMatrix());
		t("slider1=7", "7");
		t("mat2={{1,2,slider1}}", "{{1, 2, 7}}");
		Assert.assertTrue(((GeoList) get("mat2")).isEditableMatrix());
		t("mat2={{1,2,slider1},Reverse[{1,2,3}]}", "{{1, 2, 7}, {3, 2, 1}}");
		Assert.assertFalse(((GeoList) get("mat2")).isEditableMatrix());
	}

	@Test
	public void operationSequence() {
		Assert.assertEquals(StringUtil.fixVerticalBars("1..2"), "1"
				+ Unicode.ellipsis + "2");
		t("3.2..7.999", "{3, 4, 5, 6, 7, 8}");
		t("-3.2..3.2", "{-3, -2, -1, 0, 1, 2, 3}");
		t("3.2..-2", "{3, 2, 1, 0, -1, -2}");
		t("seqa=2*(1..5)", "{2, 4, 6, 8, 10}");
		assertEquals("<expression label =\"seqa\" exp=\"(2 * (1"
				+ Unicode.ellipsis + "5))\"/>",
				app.getGgbApi().getXML("seqa").split("\n")[0]);
		t("seqa=(1..3)+3", "{4, 5, 6}");
		assertEquals(
				"<expression label =\"seqa\" exp=\"(1" + Unicode.ellipsis
						+ "3) + 3\"/>",
				app.getGgbApi().getXML("seqa").split("\n")[0]);
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
	public void functionDependentPoly() {
		t("s(x,y)=x+y", "x + y");
		t("s(1,2)*x=1", "x = 0.3333333333333333");
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
	public void testIntersectCurves() {
		t("Intersect[Curve[t, t^3 - t, t, -2, 2], Curve[t, t, t, -4, 4]]",
				new String[] { "(0, 0)",
						"(1.4142135623730951, 1.4142135623730951)",
						"(-1.4142135623730951, -1.4142135623730951)" });
		t("Intersect[Curve[t, t^3 - t, t, -2, 2], Curve[t, t, t, -4, 4], 1, 1]",
				"(1.4142135623730951, 1.4142135623730956)");
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
		t("Iteration[ x*y, {1,1}, 6 ]", "720");
		t("Iteration[ x*y, {1,1}, 0 ]", "1");
		t("Iteration[ x*y, {1,1}, -1 ]", "NaN");
	}

	@Test
	public void cmdIterationList() {
		t("IterationList[ x*2, 2, 5 ]", "{2, 4, 8, 16, 32, 64}");
		t("IterationList[ a+b, a, b, {1,1}, 5 ]", "{1, 1, 2, 3, 5, 8}");
		t("IterationList[ x*y, {1,1}, 6 ]", "{1, 1, 2, 6, 24, 120, 720}");
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
		t("Sequence[ 3.2, 7.999, 1 ]", "{3, 4, 5, 6, 7, 8}");
		t("Sequence[ 3.2, 7.999, -1 ]", "?");
		t("Sequence[ -3.2, 3.2 ]", "{-3, -2, -1, 0, 1, 2, 3}");
		t("Sequence[ 3.2, -2 ]", "{3, 2, 1, 0, -1, -2}");
		t("Sequence[ t^2, t, 1, 4 ]", "{1, 4, 9, 16}");
		t("Sequence[ t^2, t, 1, 4, 2 ]", "{1, 9}");
		t("Sequence[ t^2, t, 1, 4, -2 ]", "{}");
		t("Length[Unique[Sequence[ random(), t, 1, 10]]]", "10");

	}

	@Test
	public void cmdOrthogonalPlane() {
		t("OrthogonalPlane[ (0,0,1), X=(p,2p,3p) ]", "x + 2y + 3z = 3");
		t("OrthogonalPlane[ (0,0,1), Vector[(1,2,3)] ]", "x + 2y + 3z = 3");
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

	@Test
	public void parametricSyntaxes() {
		t("X=(s,2s)", "X = (0, 0) + s (1, 2)");
		t("Intersect[X=(s,s),x+y=2]", "(1, 1)");
	}

	private static void ti(String in, String out) {
		testSyntax(in.replace("i", Unicode.IMAGINARY),
				new String[] { out.replace("i", Unicode.IMAGINARY) }, app, ap,
				StringTemplate.xmlTemplate);

	}
	@Test
	public void complexArithmetic() {
		ti("(0i)^2", "0 + 0i");
		ti("(0i)^0", "NaN - NaNi");
		ti("(0i)^-1", "NaN - NaNi");
		ti("(2+0i)^0", "1 + 0i");
		ti("(1/0+0i)^0", "NaN - NaNi");
	}

	@Test
	public void redefine() {
		t("la={1}", "{1}");
		t("lb={2}", "{2}");
		t("lc=la", "{1}");
		t("lc=lb", "{2}");
		t("1*lb", "{2}");

	}

	@Test
	public void parsePower() {
		t("a=4", "4");
		t("pia", "12.566370614359172");
		t("pix", "(" + Unicode.PI_STRING + " * x)");
		t("sinx", "sin(x)");
		t("x" + Unicode.PI_STRING, "(" + Unicode.PI_STRING + " * x)");
		t("sinxdeg", "sin((1*" + Unicode.DEGREE + " * x))");


	}

	@Test
	public void cmdSum() {
		t("listSum={1,10,1/2}", "{1, 10, 0.5}");
		t("Sum[ listSum , listSum]", "101.25");
		t("Sum[ listSum ]", "11.5");
		t("Sum[ listSum , 2 ]", "11");
		t("Sum[ listSum , 0 ]", "0");
		t("Sum[{x+y,0x+y}]", "x + y + (0 * x) + y");
		t("Sum[{x,y}]", "x + y");
		t("Sum[{(1,2),(3,4)}]", "(4, 6)");
		t("Sum[{(1,2,7),(3,4),(1,1,1)}]", "(5, 7, 8)");
		t("Sum[{\"Geo\",\"Gebra\"}]", "GeoGebra");
		t("Sum[{}]", "0");
		t("Sum[{x+y,2*x}]", "x + y + (2 * x)");
		t("Sum[x^k,k,1,5]", "x^(1) + x^(2) + x^(3) + x^(4) + x^(5)");
		t("Sum[2^k,k,1,5]", "62");
		t("Sum[(k,k),k,1,5]", "(15, 15)");
		t("y=Sum[x^k,k,1,5]", "x^(1) + x^(2) + x^(3) + x^(4) + x^(5)");
	}

	@Test
	public void cmdProduct() {
		t("Product[ {1,2,3,4} ]", "24");
		t("Product[ 1..10,  5 ]", "120");
		t("Product[ {1,2,3},  {100,1,2} ]", "18");
		t("Product[ k/(k+1),k,1,7 ]", "0.125", StringTemplate.editTemplate);
		t("Product[{x,y}]", "(x * y)");
		t("Product[ (k,k),k,1,5 ]", "-480 - 480" + Unicode.IMAGINARY);

	}

	@Test
	public void cmdPlane() {
		t("Plane[ (0,0,1),(1,0,0),(0,1,0) ]", "x + y + z = 1");
		t("Plane[ Polygon[(0,0,1),(2,0,0),(0,3,0)] ]", "3x + 2y + 6z = 6");
		t("Plane[ Ellipse[(0,0,1),(2,0,0),(0,3,0)] ]", "3x + 2y + 6z = 6");
		t("Plane[ (1,2,3),X=(s,s,s) ]", "x - 2y + z = 0");
		t("Plane[ (1,2,3),x+y+z=0 ]", "x + y + z = 6");
		t("Plane[ X=(s,s,s+1),X=(s,s,s) ]", "-x + y = 0");
		t("Plane[ (0,0,1),Vector[(1,0,0)],Vector[(0,1,0)] ]", "z = 1");
	}

	@Test
	public void cmdSurface() {
		t("Surface[u*v,u+v,u^2+v^2,u,-1,1,v,1,3]",
				"((u * v), u + v, u^(2) + v^(2))");
		t("Surface[2x,2pi]", "(u, ((2 * u) * cos(v)), ((2 * u) * sin(v)))");
		t("Surface[2x,2pi,yAxis]",
				"((cos(v) * u), ((1 - cos(v) + cos(v)) * (2 * u)), ((-sin(v)) * u))");
	}

	@Test
	public void cmdCube() {
		t("Cube[(0,0,0),(0,0,2)]",
				new String[] { "8", "(2, 0, 0)", "(0, 2, 0)", "(0, 2, 2)",
						"(2, 2, 2)", "(2, 2, 0)", "4", "4", "4", "4", "4", "4",
						"2", "2", "2", "2", "2", "2", "2", "2", "2", "2", "2",
						"2" });
		t("Cube[(0,0,0),(0,2,0),(0,2,2)]",
				new String[] { "8", "(0, 0, 2)", "(2, 0, 0)", "(2, 2, 0)",
						"(2, 2, 2)", "(2, 0, 2)", "4", "4", "4", "4", "4", "4",
						"2", "2", "2", "2", "2", "2", "2", "2", "2", "2", "2",
						"2" });
		t("Cube[(0,0,0),(0,0,2),xAxis]",
				new String[] { "8", "(0, -2, 2)", "(0, -2, 0)", "(2, 0, 0)",
						"(2, 0, 2)", "(2, -2, 2)", "(2, -2, 0)", "4", "4", "4",
						"4", "4", "4", "2", "2", "2", "2", "2", "2", "2", "2",
						"2", "2", "2",
						"2" });
	}

	@Test
	public void cmdVolume() {
		t("Volume[Cube[(0,0,1),(0,1,0)]]", eval("sqrt(8)"),
				StringTemplate.editTemplate);
		t("Volume[Sphere[(0,0,1),4]]", eval("4/3*pi*4^3"),
				StringTemplate.editTemplate);
	}

	@Test
	public void cmdSphere() {
		t("Sphere[(0,0,1),4]", indices("x^2 + y^2 + (z - 1)^2 = 16"));
		t("Sphere[(0,0,1),(0,4,1)]", indices("x^2 + y^2 + (z - 1)^2 = 16"));
	}

	@Test
	public void cmdCone() {
		t("Cone[x^2+y^2=9,4]", new String[] { eval("12*pi"),
 "X = (0, 0, 4)",
				eval("pi*15"), },
				StringTemplate.editTemplate);
		t("Cone[(0,0,0),(0,0,4),3]", new String[] { eval("12*pi"),
				"X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)",
 eval("pi*15"), },
				StringTemplate.editTemplate);
		t("Cone[(0,0,0),Vector[(0,0,4)],pi/4]",
				new String[] {
 indices("x^2 + y^2 - 1z^2 = 0") },
				StringTemplate.editTemplate);
	}

	@Test
	public void cmdCylinder() {
		t("Cylinder[x^2+y^2=9,4]", new String[] { eval("36*pi"),
				"X = (0, 0, 4) + (3 cos(t), 3 sin(t), 0)",
 eval("pi*24"), },
				StringTemplate.editTemplate);
		t("Cylinder[(0,0,0),(0,0,4),3]", new String[] { eval("36*pi"),
				"X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)",
				"X = (0, 0, 4) + (3 cos(t), 3 sin(t), 0)",
 eval("pi*24") },
				StringTemplate.editTemplate);
		t("Cylinder[(0,0,0),Vector[(0,0,4)],1]",
				new String[] { indices("x^2 + y^2 + 0z^2 = 1") },
				StringTemplate.editTemplate);
	}

	@Test
	public void cmdPlaneBisector() {
		t("PlaneBisector[(1,1),(1,1,2)]", "z = 1");
		t("PlaneBisector[Segment[(1,1),(1,1,2)]]", "z = 1");
	}

	@Test
	public void cmdInfiniteCylinder() {
		t("InfiniteCylinder[(1,1),(1,1,2),1]",
				indices("x^2 + y^2 + 0z^2 - 2x - 2y = -1"),
				StringTemplate.editTemplate);
		t("InfiniteCylinder[(1,1),Vector[(0,0,2)],1]",
				indices("x^2 + y^2 + 0z^2 - 2x - 2y = -1"),
				StringTemplate.editTemplate);
		t("InfiniteCylinder[xAxis,1]", indices("y^2 + z^2 = 1"),
				StringTemplate.editTemplate);
	}

	@Test
	public void cmdInfiniteCone() {
		t("InfiniteCone[(1,1),(1,1,2),45deg]",
				indices("x^2 + y^2 - 1z^2 - 2x - 2y = -2"),
				StringTemplate.editTemplate);
		t("InfiniteCone[(1,1),Vector[(0,0,2)],45deg]",
				indices("x^2 + y^2 - 1z^2 - 2x - 2y = -2"),
				StringTemplate.editTemplate);
		t("InfiniteCone[(1,1),xAxis,45deg]",
				indices("-1x^2 + y^2 + z^2 + 2x - 2y = 0"),
				StringTemplate.editTemplate);
	}

	@Test
	public void cmdHeight() {
		t("Height[Cone[x^2+y^2=9,4]]", "4");
		t("Height[Cube[(0,0,1),(0,0,0)]]", "1");
	}

	@Test
	public void cmdEnds() {
		t("Ends[Cone[x^2+y^2=9,4]]", new String[] {
				"X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)",
 "X = (0, 0, 4)" });
	}
	@Test
	public void cmdBottom() {
		t("Bottom[Cone[x^2+y^2=9,4]]",
				"X = (0, 0, 0) + (3 cos(t), -3 sin(t), 0)");
	}

	@Test
	public void cmdTop() {
		t("Top[Cone[x^2+y^2=9,4]]", "X = (0, 0, 4)");
	}

	@Test
	public void cmdQuadricSide() {
		t("Side[Cone[x^2+y^2=9,4]]", eval("15pi"),
				StringTemplate.editTemplate);
	}

	@Test
	public void cmdPerpendicularPlane() {
		t("PerpendicularPlane[(3,2,7),Line[(1,1,1),(1,1,3)]]", "z = 7");
		t("PerpendicularPlane[(3,2,7),Vector[(1,1,0)]]", "x + y = 5");
	}

	@Test
	public void cmdNet() {
		t("Net[Cube[(0,0,2),(0,0,0)],1]", new String[] { "24", "(0, 0, 2)",
				"(0, 0, 0)", "(2, 0, 0)", "(2, 0, 2)", "(0, 0, 4)",
				"(2, 0, 4)", "(2, 0, 6)", "(0, 0, 6)", "(-2, 0, 2)",
				"(-2, 0, 0)", "(0, 0, -2)", "(2, 0, -2)", "(4, 0, 0)",
				"(4, 0, 2)", "4", "4", "4", "4", "4", "4", "2", "2", "2", "2",
				"2", "2", "2", "2", "2", "2", "2", "2", "2", "2", "2", "2",
				"2", "2", "2" },
				StringTemplate.editTemplate);
		t("Net[Tetrahedron[(0,0,1),(0,1,0),(1,0,0)],Segment[(0,0,1),(0,1,0)]]",
				new String[] { "NaN", "(NaN, NaN, NaN)", "(NaN, NaN, NaN)",
						"(NaN, NaN, NaN)", "(NaN, NaN, NaN)",
						"(NaN, NaN, NaN)", "(NaN, NaN, NaN)", "NaN", "NaN",
						"NaN", "NaN",
						"NaN", "NaN", "NaN", "NaN", "NaN", "NaN", "NaN", "NaN",
						"NaN" });
	}

	@Test
	public void cmdIntersectPath() {
		// 3D
		t("IntersectPath[x+y+z=1,x+y-z=1]", "X = (1, 0, 0) + " + Unicode.lambda
				+ " (-2, 2, 0)");
		t("IntersectPath[x^2+y^2+z^2=4,x+y-z=1]",
				"X = (0.33333, 0.33333, -0.33333) + (-1.35401 cos(t) - 0.78174 sin(t), 1.35401 cos(t) - 0.78174 sin(t), -1.56347 sin(t))",
				StringTemplate.editTemplate);
		t("IntersectPath[Polygon[(0,0,0),(2,0,0),(2, 2,0),(0,2,0)],Polygon[(1,1),(3,1),4]]",
				new String[] { "1", "(2, 2, 0)", "(1, 2, 0)", "(1, 1, 0)",
						"(2, 1, 0)",
						"1", "1", "1", "1" },
				StringTemplate.editTemplate);
		t("IntersectPath[Polygon[(0,0),(2,0),4],x+y=3]", eval("sqrt(2)"),
				StringTemplate.editTemplate);
		// 2D
		t("IntersectPath[Polygon[(0,0,0),(2,0,0),(2, 2,0),(0,2,0)],x+y=3]",
				eval("sqrt(2)"),
				StringTemplate.editTemplate);
		t("IntersectPath[Polygon[(0,0),(2,0),4],Polygon[(1,1),(3,1),4]]",
				new String[] { "1", "(2, 2)", "(1, 2)", "(1, 1)", "(2, 1)",
						"1", "1", "1", "1" }, StringTemplate.editTemplate);
	}

	private static String indices(String string) {
		return string.replace("^2", Unicode.Superscript_2 + "");
	}

	private static String eval(String string) {
		return ap.evaluateToNumeric(string, true)
				.toValueString(StringTemplate.editTemplate);
	}

	@Test
	public void cmdDodecahedron() {
		String[] dodeca = new String[] { "7.66312",
				"(1.30902, 0.95106, 0)", "(0.5, 1.53884, 0)",
				"(-0.30902, -0.42533, 0.85065)",
				"(1.30902, -0.42533, 0.85065)", "(1.80902, 1.11352, 0.85065)",
				"(0.5, 2.06457, 0.85065)", "(-0.80902, 1.11352, 0.85065)",
				"(-0.80902, 0.26287, 1.37638)", "(0.5, -0.68819, 1.37638)",
				"(1.80902, 0.26287, 1.37638)", "(1.30902, 1.80171, 1.37638)",
				"(-0.30902, 1.80171, 1.37638)", "(-0.30902, 0.42533, 2.22703)",
				"(0.5, -0.16246, 2.22703)", "(1.30902, 0.42533, 2.22703)",
				"(1, 1.37638, 2.22703)", "(0, 1.37638, 2.22703)", "1.72048",
				"1.72048", "1.72048", "1.72048", "1.72048", "1.72048",
				"1.72048", "1.72048", "1.72048", "1.72048", "1.72048",
				"1.72048", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1", "1", "1", "1", "1", "1", "1" };
		platonicTest("Dodecahedron", 108, dodeca);
	}

	private static void platonicTest(String string, int deg, String[] dodeca) {
		t(string + "[(1;" + deg + "deg),(0,0)]", dodeca,
				StringTemplate.editTemplate);
		t(string + "[(1;" + deg + "deg),(0,0),(1,0)]", dodeca,
				StringTemplate.editTemplate);
		String[] dodeca1 = new String[dodeca.length + 1];
		dodeca1[0] = dodeca[0];
		dodeca1[1] = "(1, 0, 0)";
		for (int i = 2; i < dodeca1.length; i++) {
			dodeca1[i] = dodeca[i - 1];
		}
		t(string + "[(1;" + deg + "deg),(0,0),Vector[(0,0,1)]]", dodeca1,
				StringTemplate.editTemplate);

	}

	@Test
	public void cmdIcosahedron() {
		String[] dodeca = new String[] { "2.18169",
				"(-0.30902, 0.75576, 0.57735)", "(0.5, -0.6455, 0.57735)",
				"(1.30902, 0.75576, 0.57735)", "(0.5, 1.22285, 0.93417)",
				"(-0.30902, -0.17841, 0.93417)",
				"(1.30902, -0.17841, 0.93417)", "(0, 0.57735, 1.51152)",
				"(0.5, -0.28868, 1.51152)", "(1, 0.57735, 1.51152)", "0.43301",
				"0.43301", "0.43301", "0.43301", "0.43301", "0.43301",
				"0.43301", "0.43301", "0.43301", "0.43301", "0.43301",
				"0.43301", "0.43301", "0.43301", "0.43301", "0.43301",
				"0.43301", "0.43301", "0.43301", "0.43301", "1", "1", "1", "1",
				"1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1" };
		platonicTest("Icosahedron", 60, dodeca);
	}

	@Test
	public void cmdOctahedron() {
		String[] dodeca = new String[] { "0.4714", "(0, 0.57735, 0.8165)",
				"(0.5, -0.28868, 0.8165)", "(1, 0.57735, 0.8165)", "0.43301",
				"0.43301", "0.43301", "0.43301", "0.43301", "0.43301",
				"0.43301", "0.43301", "1", "1", "1", "1", "1", "1", "1", "1",
				"1", "1", "1", "1" };
		platonicTest("Octahedron", 60, dodeca);
	}

	@Test
	public void cmdPyramid() {


		t("Pyramid[(0,0,0),(1,0,0),(0,1,0),(0,0,1)]", new String[] {
				eval("1/6"), "0.5", "0.5", eval("sqrt(3)/2"), "0.5", "1",
				eval("sqrt(2)"), "1", "1", eval("sqrt(2)"), eval("sqrt(2)"),
 },
				StringTemplate.editTemplate);
		t("Pyramid[Polygon[(0,0,0),(1,0,0),(0,1,0)],(0,0,1)]", new String[] {
				eval("1/6"), "0.5", eval("sqrt(3)/2"), "0.5", "1",
				eval("sqrt(2)"), eval("sqrt(2)"),
 },
				StringTemplate.editTemplate);
		t("Pyramid[Polygon[(-3,0,0),(0,-3,0),(3,0,0),(0,3,0)],4]",
				new String[] {
 "24", "(0, 0, 4)", "9.60469", "9.60469",
						"9.60469", "9.60469", "5", "5", "5", "5" },
				StringTemplate.editTemplate);
	}

	@Test
	public void cmdPrism() {
		t("Prism[(0,0,0),(1,0,0),(0,1,0),(0,0,1)]", new String[] { "0.5",
				"(1, 0, 1)", "(0, 1, 1)", "0.5", "1", eval("sqrt(2)"), "1",
				"0.5", "1", eval("sqrt(2)"), "1", "1", "1", "1", "1",
				eval("sqrt(2)"), "1" },
				StringTemplate.editTemplate);
		t("Prism[Polygon[(0,0,0),(1,0,0),(0,1,0)],(0,0,1)]", new String[] {
				"0.5", "(1, 0, 1)", "(0, 1, 1)", "1", eval("sqrt(2)"), "1",
				"0.5", "1", "1", "1", "1", eval("sqrt(2)"), "1" },
				StringTemplate.editTemplate);
		t("Prism[Polygon[(-3,0,0),(0,-3,0),(3,0,0),(0,3,0)],4]", new String[] {
				"72", "(-3, 0, 4)", "(0, -3, 4)", "(3, 0, 4)", "(0, 3, 4)",
				eval("12sqrt(2)"), eval("12sqrt(2)"), eval("12sqrt(2)"),
				eval("12sqrt(2)"), "18", "4", "4",
 "4", "4", eval("3sqrt(2)"),
				eval("3sqrt(2)"),
				eval("3sqrt(2)"), eval("3sqrt(2)") },
				StringTemplate.editTemplate);
	}

	@Test
	public void cmdTetrahedron() {
		String[] dodeca = new String[] { "0.11785", "(0.5, 0.28868, 0.8165)",
				"0.43301", "0.43301", "0.43301", "0.43301", "1", "1", "1", "1",
				"1", "1" };
		platonicTest("Tetrahedron", 60, dodeca);
	}

	@Test
	public void cmdOrthogonalLine() {
		// 2D
		t("PerpendicularLine[ (1,2), x+y=7 ]", "-x + y = 1");
		t("PerpendicularLine[ (1,2), Segment[(1,6),(6,1)] ]", "-x + y = 1");
		t("PerpendicularLine[ (1,2),Vector[(1,3)]]", "-x - 3y = -7");
		// 3D
		t("PerpendicularLine[ (1,2,0), x+y=7 ]", "X = (1, 2, 0) + "
				+ Unicode.lambda + " (1, 1, 0)");
		t("PerpendicularLine[ (1,2,0), Segment[(1,6),(6,1)] ]",
				"X = (1, 2, 0) + " + Unicode.lambda + " (-5, -5, 0)");
		t("PerpendicularLine[ (1,2,0),Vector[(1,3)]]", "X = (1, 2, 0) + "
				+ Unicode.lambda + " (3, -1, 0)");
		t("PerpendicularLine[(1,1,1),z=0]", "X = (1, 1, 1) + " + Unicode.lambda
				+ " (0, 0, -1)");
		t("PerpendicularLine[(1,1,1),y=0,xOyPlane]", "X = (1, 1, 1) + "
				+ Unicode.lambda + " (0, 1, 0)");
		t("PerpendicularLine[(1,1,1),y=0,space]", "X = (1, 1, 1) + "
				+ Unicode.lambda + " (0, 0.70711, 0.70711)",
				StringTemplate.editTemplate);
		t("PerpendicularLine[x=1,y=1]", "X = (1, 1, 0) + " + Unicode.lambda
				+ " (0, 0, 1)");
	}

	@Test
	public void testExpIntegral() {
		t("expIntegral(5)", "40.18528", StringTemplate.editTemplate);
		t("expIntegral(5+0i)", "40.18528 + 0" + Unicode.IMAGINARY,
				StringTemplate.editTemplate);
	}

	@Test
	public void testInverseTrigDegree(){
		t("asind(0.5)", "30\u00B0", StringTemplate.editTemplate);
		t("acosd(0.5)", "60\u00B0", StringTemplate.editTemplate);
		t("atand(1)", "45\u00B0", StringTemplate.editTemplate);
		t("asind(0.317)", "18.48159\u00B0", StringTemplate.editTemplate);
		t("acosd(0.317)", "71.51841\u00B0", StringTemplate.editTemplate);
		t("atand(0.317)", "17.58862\u00B0", StringTemplate.editTemplate);
	
	}

	@Test
	public void cmdSpline() {
		String theSpline = "(If[t  <  0.38743, 0.88246t^3 + 0t^2 + 2.44868t, t  <  1, -0.55811t^3 + 1.67434t^2 + 1.8t + 0.08377], If[t  <  0.38743, -5.43794t^3 + 0t^2 + 3.39737t, t  <  1, 3.43925t^3 - 10.31776t^2 + 7.39473t - 0.51623])";
		t("Spline[{(0,0),(1,1),(3,0)}]", unicode(theSpline),
				StringTemplate.editTemplate);
		t("Spline[{(0,0),(1,1),(3,0)},3]", unicode(theSpline),
				StringTemplate.editTemplate);
		t("Spline[{(0,0),(1,1),(3,0)},3,sqrt(x^2+y^2)]",
				unicode(theSpline),
				StringTemplate.editTemplate);
		t("Spline[{(0,0),(1,1),(1,1),(3,0)},4]", "?",
				StringTemplate.editTemplate);
	}

	@Test
	public void cmdPolynomial() {
		t("Polynomial[ sin(x) ]", "?");
		t("Polynomial[ 1*x^2-1*x+1 ]", "x^(2) - x + 1");
		t("Polynomial[ -x*(x+1)*(x-1) ]", "(-x^(3)) + x");
		t("Polynomial[ (2x+3)^3 ]",
				"(8 * x^(3)) + (36 * x^(2)) + (54 * x) + 27");
		t("Polynomial[ {(1,1),(-1,1),(0,0) } ]", "x^(2)");
		t("Polynomial[ {(1,0),(-1,2),(0,0) } ]", "x^(2) - x");
	}

	@Test
	public void cmdRandomPolynomial() {
		app.setRandomSeed(42);
		t("RandomPolynomial[5,-1,1]", "x^(5) - x^(4) + x^(3) - x^(2) - x + 1");
		t("RandomPolynomial[5,-1,1]", "(-x^(5)) + x^(4) + x^(3) + x + 1");
		t("RandomPolynomial[5,-1,1]", "x^(5) - x^(4) + x^(3) + x^(2) - x - 1");
		t("RandomPolynomial[5,-1,1]", "(-x^(5)) + x^(4) + x^(2) + x");
		t("RandomPolynomial[5,-1,1]", "(-x^(5)) - x + 1");
		t("RandomPolynomial[5,-1,1]", "(-x^(5)) + x^(4) + x^(3) + x^(2) - x");
		t("RandomPolynomial[5,-1,1]", "x^(5) + x^(4) - x^(3) - 1");
		t("RandomPolynomial[5,-2,2]",
				"(2 * x^(5)) + (2 * x^(3)) - (2 * x^(2)) + 1");
		t("RandomPolynomial[5,-3,3]",
				"(2 * x^(5)) - x^(4) - (3 * x^(3)) + (2 * x^(2)) + 3");
		t("RandomPolynomial[5,-5,4]",
				"(-5 * x^(5)) - (4 * x^(4)) + (4 * x^(3)) - (2 * x^(2)) - (5 * x) - 5");
		t("RandomPolynomial[5,-2,5]",
				"x^(5) + (5 * x^(4)) - x^(3) + (4 * x) + 1");
	}

	@Test
	public void testIndexLookup() {
		t("aa_{1}=1", "1");
		t("aa_{1}+1", "2");
		t("aa_1+1", "2");
		t("ab_1=1", "1");
		t("ab_{1}+1", "2");
		t("ab_1+1", "2");
		// overwrite
		t("ab_1=3", "3");
		t("ab_{1}+1", "4");
	}

	@Test
	public void cmdUnion() {
		t("join=Union[Polygon[(1,1),(1,0),(0,1)],Polygon[(0,0),(1,0),(0,1)]]",
				new String[] { "1", "(1, 0)", "(1, 1)", "(0, 1)", "(0, 0)", "1",
						"1", "1", "1" });
		t("join=Union[Polygon[(1,1,0),(1,0,0),(0,1,0)],Polygon[(0,0,0),(1,0,0),(0,1,0)]]",
				new String[] { "1", "(1, 0, 0)", "(0, 0, 0)", "(0, 1, 0)",
						"(1, 1, 0)", "1", "1", "1", "1" });
		t("Union[{1,2,3}, {2,2,2,4,4,4}]", "{1, 2, 3, 4}");
		t("Union[{\"1\",\"2\",\"3\"}, {\"2\",\"2\",\"2\",\"4\",\"4\",\"4\"}]",
				"{\"1\", \"2\", \"3\", \"4\"}");
	}

	@Test
	public void cmdInvert() {
		t("Invert[ {{1,1},{0,2}} ]", "{{1, -0.5}, {0, 0.5}}");
		t("Invert[ sin(x) ]", "asin(x)");
		app.getSettings().getCasSettings().setEnabled(false);
		app.getKernel().getAlgebraProcessor().reinitCommands();
		t("Invert[ sin(x) ]", "NInvert[sin(x)]");
		app.getSettings().getCasSettings().setEnabled(true);
	}

	@Test
	public void cmdNInvert() {
		t("ni(x)=Invert[ sin(x) ]", "NInvert[sin(x)]");
		t("ni(sin(1))", "1");
	}

	@Test
	public void testShorthandIntersect() {
		t("x=2*y=3*z", "X = (0, 0, 0) + " + Unicode.lambda + " (6, 3, 2)");
		t("(x=2y,2y=3z)", "X = (0, 0, 0) + " + Unicode.lambda + " (6, 3, 2)");
		t("x-1=y+2=z-6",
				"X = (-2.3333333333333335, -5.333333333333333, 2.6666666666666665) + "
						+ Unicode.lambda + " (1, 1, 1)");
		t("(x-1)/3=(y+2)/2=5(z-6)",
				"X = (-60.47239263803682, -42.981595092024556, 1.9018404907975464) + "
						+ Unicode.lambda
						+ " (2.5, 1.6666666666666665, 0.16666666666666666)");
		t("1-x=y+2=z-6",
				"X = (4.333333333333334, -5.333333333333333, 2.6666666666666665) + "
						+ Unicode.lambda + " (1, -1, -1)");
		t("x+x-1=y+y+2=z-6+z",
				"X = (-1.1666666666666667, -2.6666666666666665, 1.3333333333333333) + "
						+ Unicode.lambda + " (4, 4, 4)");
	}

	@Test
	public void cmdFit() {
		t("Fit[ {(0,1),(1,2),(2,5)}, {x^2,x,1} ]", unicode("1x^2 + 0x + 1 (1)"),
				StringTemplate.editTemplate);
		t("Fit[ {(0,1,1),(1,1,2),(2,1,5),(0,2,4),(1,2,5),(2,2,8)}, {x^2,x,1,x^2*y,x*y,y} ]",
				unicode("3y + 0x y + 0x^2 y - 2 + 0x + 1x^2"),
				StringTemplate.editTemplate);
		t("a=Slider[0,10]", "0");
		t("b=Slider[0,10]", "0");
		t("c=Slider[0,10]", "0");
		t("Fit[ {(0,1),(1,2),(2,5)},a*x^2+b*x+c ]",
				unicode("1x^2 + 0x + 1"),
				StringTemplate.editTemplate);

	}

	@Test
	public void zipReloadTest() {
		t("list1=Zip[f(1),f,{x,x+1}]", "{1, 2}");
		String xml = app.getGgbApi().getXML();
		t("list2=Zip[f(1,2),f,{x+y,y+x+1}]", "{3, 4}");
		app.getKernel().clearConstruction(true);
		app.getGgbApi().setXML(xml);
		t("list1", "{1, 2}");
		t("Object[\"list2\"]", "NaN");
	}

	@Test
	public void conditionalDerivativeTest() {
		t("f(x)=If[x>0,x^2]", "If[x > 0, x^(2)]");
		t("f'(x)=Derivative[f]", "If[x > 0, (2 * x)]");
		t("f'(3)", "6");
		t("g(x,y)=If[x+y>0,x^2+x*y]", "If[x + y > 0, x^(2) + (x * y)]");
		t("h(x,y)=Derivative[g, x]", "If[x + y > 0, (2 * x) + y]");
		t("h(1,3)", "5");
	}

	@Test
	public void redefineTest() {
		t("A=(1,1)", "(1, 1)");
		t("B=(1,0)", "(1, 0)");
		t("C=(0,0)", "(0, 0)");
		t("D=(0,1)", "(0, 1)");
		t("poly1=Polygon[A,B,C,D]", new String[] { "1", "1", "1", "1", "1" });
		t("a", "1"); // polygon side
		app.getKernel().setUndoActive(true);
		app.getKernel().initUndoInfo();
		app.storeUndoInfo();
		checkError("A(x)=x", "Redefinition failed");
		t("A", "(1, 1)");
		t("poly1", "1");
		t("a", "1");

	}

	static String unicode(String theSpline) {
		return theSpline.replace("^2", Unicode.Superscript_2 + "").replace("^3",
				Unicode.Superscript_3 + "").replace("deg", Unicode.DEGREE);
	}
}
