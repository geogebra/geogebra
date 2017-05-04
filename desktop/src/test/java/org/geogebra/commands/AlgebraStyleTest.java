package org.geogebra.commands;

import java.util.Locale;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.lang.Unicode;
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

	private static void checkEquation(String def, int mode, String check) {
		GeoElementND[] el = ap.processAlgebraCommandNoExceptionHandling(def,
				false, TestErrorHandler.INSTANCE, false, null);
		((GeoConicND) el[0]).setToStringMode(mode);
		assertEquals(check.replace("^2", Unicode.Superscript_2 + ""),
				el[0].toValueString(StringTemplate.defaultTemplate));
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

	@Test
	public void checkEquationExplicit() {
		checkEquation("x^2+4*y^2=1", GeoConicND.EQUATION_EXPLICIT,
				"x^2 + 4y^2 = 1");
		checkEquation("x^2+4*y^2-y+x*y=x +x -1", GeoConicND.EQUATION_EXPLICIT,
				"x^2 + x y + 4y^2 - 2x - y = -1");
		checkEquation("-x^2=x +x -1", GeoConicND.EQUATION_EXPLICIT,
				"-x^2 - 2x = -1");
	}

	@Test
	public void checkEquationVertex() {
		// ellipse: fallback to explicit
		checkNonParabolaFallback(GeoConicND.EQUATION_VERTEX);
		// three actual parabolas
		checkEquation("-x^2=x +x -1+y", GeoConicND.EQUATION_VERTEX,
				"y = -(x + 1)^2 +2");
		checkEquation("x^2=x +x -1+y", GeoConicND.EQUATION_VERTEX,
				"y = (x - 1)^2");
		checkEquation("y^2=y +y -1+x", GeoConicND.EQUATION_VERTEX,
				"(x - 0) = (y - 1)^2");
	}

	@Test
	public void checkEquationSpecific() {
		// ellipse
		checkEquation("x^2+4*y^2=1", GeoConicND.EQUATION_SPECIFIC,
				"x^2 / 1 + y^2 / 0.25 = 1");
		// hyperbola
		checkEquation("x^2-4*y^2=2x+2y+1", GeoConicND.EQUATION_SPECIFIC,
				"(x - 1)^2 / 1.75 - (y + 0.25)^2 / 0.44 = 1");
		// double line
		checkEquation("-x^2=x +x -1", GeoConicND.EQUATION_SPECIFIC,
				"(-x - 2.41) (-x + 0.41) = 0");
		// parabolas
		checkEquation("-x^2-x=x -1+y", GeoConicND.EQUATION_SPECIFIC,
				"x^2 = -2x - y + 1");
		checkEquation("y^2=x +x -1+y", GeoConicND.EQUATION_SPECIFIC,
				"y^2 = 2x + y - 1");
		checkEquation("(x+y)^2=x +x -1+y", GeoConicND.EQUATION_SPECIFIC,
				"x^2 + 2x y + y^2 - 2x - y = -1");
	}

	@Test
	public void checkEquationConicform() {
		checkNonParabolaFallback(GeoConicND.EQUATION_CONICFORM);
		// parabolas
		checkEquation("-x^2-x=x -1+y", GeoConicND.EQUATION_CONICFORM,
				"-(y - 2) = (x + 1)^2");
		checkEquation("y^2=x +x -1+y", GeoConicND.EQUATION_CONICFORM,
				"2(x - 0.38) = (y - 0.5)^2");
		checkEquation("(x+y)^2=x +x -1+y", GeoConicND.EQUATION_CONICFORM,
				"x^2 + 2x y + y^2 - 2x - y = -1");
	}

	@Test
	public void checkEquationParametric() {
		// ellipse
		checkEquation("x^2+4*y^2=1", GeoConicND.EQUATION_PARAMETRIC,
				"X = (0, 0) + (cos(t), 0.5 sin(t))");
		// hyperbola
		checkEquation("x^2-4*y^2=2x+2y+1", GeoConicND.EQUATION_PARAMETRIC,
				"X = (1, -0.25) + (" + Unicode.PLUSMINUS
						+ " 1.32 cosh(t), 0.66 sinh(t))");
		// double line TODO wrong
		checkEquation("-x^2=x +x -1", GeoConicND.EQUATION_PARAMETRIC,
				"X = (-1 " + Unicode.PLUSMINUS + " 1.41, 0, 0) + "
						+ Unicode.lambda + " (0, 1, 0)");
		// parabolas
		checkEquation("-x^2-x=x -1+y", GeoConicND.EQUATION_PARAMETRIC,
				"X = (-1, 2) + (-0.5 t, -0.25 t^2)");
		checkEquation("y^2=x +x -1+y", GeoConicND.EQUATION_PARAMETRIC,
				"X = (0.38, 0.5) + (0.5 t^2, t)");
		checkEquation("(x+y)^2=x +x -1+y", GeoConicND.EQUATION_PARAMETRIC,
				"X = (0.81, -0.06) + (0.06 t^2 + 0.13 t, -0.06 t^2 + 0.13 t)");
	}

	@Test
	public void checkEquationImplicit() {
		// ellipse
		checkEquation("x^2+4*y^2=1", GeoConicND.EQUATION_IMPLICIT,
				"x^2 + 4y^2 = 1");
		// hyperbola
		checkEquation("x^2-4*y^2=2x+2y+1", GeoConicND.EQUATION_IMPLICIT,
				"x^2 - 4y^2 - 2x - 2y = 1");
		// double line TODO wrong
		checkEquation("-x^2=x +x -1", GeoConicND.EQUATION_IMPLICIT,
				"-x^2 - 2x = -1");
		// parabolas
		checkEquation("-x^2-x=x -1+y", GeoConicND.EQUATION_IMPLICIT,
				"-x^2 - 2x - y = -1");
		checkEquation("y^2=x +x -1+y", GeoConicND.EQUATION_IMPLICIT,
				"y^2 - 2x - y = -1");
		checkEquation("(x+y)^2=x +x -1+y", GeoConicND.EQUATION_IMPLICIT,
				"x^2 + 2x y + y^2 - 2x - y = -1");
	}

	private static void checkNonParabolaFallback(int mode) {
		// ellipse
		checkEquation("x^2+4*y^2=1", mode, "x^2 + 4y^2 = 1");
		// hyperbola
		checkEquation("x^2-4*y^2=2x+2y+1", mode, "x^2 - 4y^2 - 2x - 2y = 1");
		// double line
		checkEquation("-x^2=x +x -1", mode, "-x^2 - 2x = -1");

	}


}
