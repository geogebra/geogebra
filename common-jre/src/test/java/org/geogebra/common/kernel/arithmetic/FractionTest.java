package org.geogebra.common.kernel.arithmetic;

import static org.hamcrest.MatcherAssert.assertThat;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.test.OrderingComparison;
import org.junit.Test;

public class FractionTest extends SymbolicArithmeticTest {

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
		for (int k = 2; k <= 50; k++) {
			add("A" + k + "=A" + (k - 1) + "+ B" + (k - 1) + "-A" + (k - 1));
			add("B" + k + "=B" + (k - 1) + "+ A" + (k - 1) + "-B" + (k - 1));
		}
		GeoElement a50 = lookup("A50");
		((GeoNumeric) a50).setSymbolicMode(true, false);
		// A and B were swapped 49 times
		assertThat(a50, hasValue("3 / 2"));
		assertThat(System.currentTimeMillis() - time, OrderingComparison.lessThan(1000L));
	}
}
