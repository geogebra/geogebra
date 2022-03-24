package org.geogebra.common.kernel.arithmetic;

import org.junit.Test;

public class SurdTest extends SymbolicArithmeticTest {

	@Test
	public void testSurd() {
		t("sqrt(8)", "2\u221a(2)");
		t("sqrt(1024 * 2 * 3)", "32\u221a(6)");
		// Calculate simplified surds for integers only
		t("sqrt(1.3)", "1.14");
		t("sqrt(-3)", "?");
	}
}
