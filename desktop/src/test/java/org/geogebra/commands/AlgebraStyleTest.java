package org.geogebra.commands;

import java.util.Locale;

import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.desktop.main.AppDNoGui;
import org.geogebra.desktop.main.LocalizationD;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AlgebraStyleTest extends Assert {
	static AppDNoGui app;
	static AlgebraProcessor ap;




	private static void checkRows(String def, int rows) {
		GeoElementND[] el = ap.processAlgebraCommandNoExceptionHandling(def,
				false,
				TestErrorHandler.INSTANCE, false, null);
		assertEquals(rows, el[0].needToShowBothRowsInAV() ? 2 : 1);
	}

	
	@Before
	public void resetSyntaxes(){
		app.getKernel().clearConstruction(true);
	}
	
	@BeforeClass
	public static void setupApp() {
		app = new AppDNoGui(new LocalizationD(3), true);
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
		checkRows("Sequence[100]", 2);

	}
}
