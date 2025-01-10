package org.geogebra.common.kernel.commands;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class TranslateTest extends BaseUnitTest {

	@Test
	public void testTranslatePiecewise() {
		add("k = 1");
		add("f(x) = If(0 < x < k, 2 + x, 1 < x < 2, -1)");
		add("u = Vector((0, 0), (4, 0))");
		add("f_1(x) = Translate(f, u)");
		t("f_1(4.5)", "2.5");
	}
}
