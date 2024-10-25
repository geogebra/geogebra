package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.OrderingComparison;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

public class FractionTest extends SymbolicArithmeticTest {

	@Before
	public void setupRounding() {
		getKernel().setPrintDecimals(10);
	}

	@Test
	public void functionWithFractions() {
		t("frac(x)=(3/2)^x", "(3 / 2)^x");
		t("frac(2)", "9 / 4");
		t("frac(-1)", "2 / 3");
		t("frac(-2)", "4 / 9");
	}

	@Test
	public void scientificNotation() {
		t("5*10^(-2)", "1 / 20");
	}

	@Test
	public void performanceCheck() {
		long time = System.currentTimeMillis();
		add("A1=1/2");
		add("B1=3/2");
		for (int k = 2; k <= 12; k++) {
			add("A" + k + "=A" + (k - 1) + "+ B" + (k - 1) + "-A" + (k - 1));
			add("B" + k + "=B" + (k - 1) + "+ A" + (k - 1) + "-B" + (k - 1));
		}
		GeoElement a12 = lookup("A12");
		((GeoNumeric) a12).setSymbolicMode(true, false);
		// A and B were swapped 11 times
		assertThat(a12, hasValue("3 / 2"));
		assertThat(System.currentTimeMillis() - time, OrderingComparison.lessThan(1000L));
	}

	@Test
	public void testExactFractionDecimals() {
		getKernel().setPrintDecimals(2);
		assertThat(add("1/3"), hasExactFraction(false));
		assertThat(add("1/1000"), hasExactFraction(false));
		assertThat(add("1/2"), hasExactFraction(true));
		assertThat(add("-1/2"), hasExactFraction(true));
		assertThat(add("1/20"), hasExactFraction(true));
		assertThat(add("500/1000"), hasExactFraction(true));
		assertThat(add("501/1000"), hasExactFraction(false));
		assertThat(add("51/1000"), hasExactFraction(false));
	}

	@Test
	public void testExactFractionPi() {
		assertThat(add("(3 pi)/2"), hasExactFraction(false));
		assertThat(add("pi/2"), hasExactFraction(false));
	}

	@Test
	public void testExactFractionSignificantDigits() {
		getKernel().setPrintFigures(2);
		assertThat(add("1/3"), hasExactFraction(false));
		assertThat(add("1/1000"), hasExactFraction(true));
		assertThat(add("1/2"), hasExactFraction(true));
		assertThat(add("1/20"), hasExactFraction(true));
		assertThat(add("500/1000"), hasExactFraction(true));
		assertThat(add("501/1000"), hasExactFraction(false));
		assertThat(add("51/1000"), hasExactFraction(true));
	}

	private Matcher<GeoElement> hasExactFraction(boolean flag) {
		return hasProperty("exact fraction",
				geo -> Fractions.isExactFraction(geo, getKernel()), flag);
	}
}
