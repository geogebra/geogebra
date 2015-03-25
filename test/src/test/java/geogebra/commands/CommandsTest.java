package geogebra.commands;

import geogebra.CommandLineArguments;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.commands.AlgebraProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.main.AppD;
import geogebra3D.App3D;

import java.util.Locale;

import javax.swing.JFrame;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommandsTest extends Assert{
	static AppD app;
	static AlgebraProcessor ap;

	private static void  t(String input, String expected){
		testSyntax(input,expected,app,ap);
	}
	public static void testSyntax(String s,String expected,App app,AlgebraProcessor ap) {
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
		String actual = result[0].toValueString(StringTemplate.xmlTemplate);
		Assert.assertEquals(s + ":"+actual, expected, actual);
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
	
}
