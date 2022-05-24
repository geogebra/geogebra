package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalFunction.isSupported;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class IntervalFunctionTest extends BaseUnitTest {

	@Test
	public void supportIf() {
		assertTrue(isSupported(add("If[x < 1, 0]")));
		assertTrue(isSupported(add("If[x < 1, 2x]")));
		assertTrue(isSupported(add("If[x < 1, x + 1]")));
		assertTrue(isSupported(add("If[sin(x) < 0, x + 1]")));
		assertFalse(isSupported(add("If[x < 1, 2x + x^3")));
	}

	@Test
	public void supportIfElse() {
		assertTrue(isSupported(add("If[x < 1, x, x + 1]")));
		assertTrue(isSupported(add("If[sin(x) < 0, x, x^2 + 1]")));
		assertFalse(isSupported(add("If[x < 1, x * sin(x), x + 1]")));
		assertFalse(isSupported(add("If[x < 1, x, x * sin(x)]")));
		assertFalse(isSupported(add("If[x < 1, x/tan(x), x * sin(x)]")));
		assertFalse(isSupported(add("If[x < 1, x * sin(x) + 1]")));
	}

	@Test
	public void supportIfList() {
		assertTrue(isSupported(add("if(x < -2, -2, x > 0, 4)")));
		assertTrue(isSupported(add("if(x < -2, x + 1, x > 0, x^4)")));
		assertFalse(isSupported(add("if(x < -2, x * (ln(x)), x > 0, x^4)")));
	}
}