package org.geogebra.commands;

import java.util.Locale;

import javax.swing.JFrame;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.CommandLineArguments;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.main.AppD;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AlgebraStyleTest extends Assert {
	static AppD app;
	static AlgebraProcessor ap;




	private void checkRows(String def, int rows) {
		GeoElement[] el = ap.processAlgebraCommandNoExceptionHandling(def,
				false,
				TestErrorHandler.INSTANCE, false, null);
		assertEquals(rows, el[0].needToShowBothRowsInAV() ? 2 : 1);
	}

	private static int syntaxes = -1000;
	
	@Before
	public void resetSyntaxes(){
		syntaxes = -1000;
		app.getKernel().clearConstruction(true);
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
		// make sure x=y is a line, not plane
		app.getGgbApi().setPerspective("1");
	    // Setting the general timeout to 11 seconds. Feel free to change this.
		app.getKernel().getApplication().getSettings().getCasSettings().setTimeoutMilliseconds(11000);
	}

	
	@Test
	public void twoRowsAlgebra() {
		checkRows("a=1", 1);
		checkRows("a+a", 2);
		checkRows("sqrt(x+a)", 2);
		checkRows("{a}", 2);
		checkRows("{x}", 1);
		checkRows("{x+a}", 2);
		checkRows("{{1}}", 1);
		checkRows("{{a}}", 2);
		checkRows("{{a}}+{{1}}", 2);
		checkRows("{x=y}", 2);
		checkRows("x=y", 2);
		checkRows("{y=x}", 1);

	}
}
