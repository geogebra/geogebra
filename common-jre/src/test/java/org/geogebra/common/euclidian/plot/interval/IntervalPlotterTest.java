package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.BaseUnitTest;
import org.junit.Before;
import org.junit.Test;

public class IntervalPlotterTest extends BaseUnitTest {

	private final IntervalPlotterCommon common = new IntervalPlotterCommon();

	@Before
	public void setUp() throws Exception {
		common.setup();
	}

	@Test
	public void sinX() {
		common.withDefaultScreen();
		common.withFunction("sin(x)");
		common.valuesShouldBeBetween(-1, 1);
	}

	@Test
	public void sinXInverseInverse() {
		common.withDefaultScreen();
		common.withFunction("1/(1/sin(x))");
		common.valuesShouldBeBetween(-1, 1);
	}

	@Test
	public void secSecXInverse() {
		common.withDefaultScreen();
		common.withFunction("1/sec(sec(x))");
		common.valuesShouldBeBetween(-1, 1);
	}

	@Test
	public void cscTanXInverse() {
		common.withDefaultScreen();
		common.withFunction("1/csc(tan(x))");
		common.valuesShouldBeBetween(-1, 1);
	}

	@Test
	public void sqrtLnSecX() {
		common.withBounds(-2, 10, -15, -15);
		common.withScreenSize(100, 100);
		common.withFunction("sqrt(ln(sec(x)))");
		common.valuesShouldBeBetween(0, 4.4);
	}

	@Test
	public void lnCosX() {
		common.withBounds(-2, 2, -15, -15);
		common.withScreenSize(500, 100);
		common.withFunction("ln(cos(x))");
		common.valuesShouldBeBetween(-19, 0);
	}

	@Test
	public void cotLnCotX() {
		common.withBounds(0, 3.2, -15, -15);
		common.withScreenSize(100, 100);
		common.withFunction("cot(ln(cot(x)))");
		common.valuesShouldBeBetween(-19, 0);
	}

	@Test
	public void sqrtSecCotX() {
		common.withDefaultScreen();
		common.withFunction("sqrt(sec(cot(x)))");
		common.valuesShouldNotBe(entry -> entry.y - 625 > 1);
	}

	@Test
	public void fillUpDownNoBetween() {
		common.withBounds(-0.6, 0.6, -10, -10);
		common.withScreenSize(100, 100);
		common.withFunction("sec((-9/x))");
		common.valuesShouldNotBe(entry -> entry.y < 1 - 1E-6 && entry.y > -1 + 1E-6);

	}

	@Test
	public void secMinus9InverseX() {
		common.withBounds(-1, 1, -10, -10);
		common.withScreenSize(100, 100);
		common.withFunction("cot(1/tan(x))");
		common.valuesShouldBeBetween(-1, 10);
	}

}
