package org.geogebra.common.kernel.arithmetic;

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
}
