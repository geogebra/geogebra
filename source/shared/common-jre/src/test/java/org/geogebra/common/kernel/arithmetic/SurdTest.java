package org.geogebra.common.kernel.arithmetic;

import org.junit.Test;

public class SurdTest extends SymbolicArithmeticTest {

	@Test
	public void testSurd() {
		t("sqrt(8)", "2\u221a(2)");
		t("sqrt(1024 * 2 * 3)", "32\u221a(6)");
	}

	@Test
	public void testOne() {
		t("sqrt(1)", "1");
	}

	@Test
	public void testNegativeNumbers() {
		t("sqrt(-3)", "?");
	}

	@Test
	public void testRationalNumbers() {
		t("sqrt(1.3)", "1.14");
	}

	@Test
	public void testPrimes() {
		t("sqrt(17)", "4.12");
	}

	@Test
	public void testLargeNumbers() {
		t("sqrt(32614907904)", "180595.98");
	}
}
