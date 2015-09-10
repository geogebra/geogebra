package org.geogebra.commands;

import java.util.Locale;

import javax.swing.JFrame;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
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
				new String[]{"--silent"}), new JFrame(), false);
		app.setLanguage(Locale.US);
		ap = app.getKernel().getAlgebraProcessor();
	    // Setting the general timeout to 11 seconds. Feel free to change this.
		app.getKernel().getApplication().getSettings().getCasSettings().setTimeoutMilliseconds(11000);
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

}
