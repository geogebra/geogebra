package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class IntervalFunctionTest extends BaseUnitTest {

	@Test
	public void supportIfCommand() {
		assertTrue(IntervalFunction.isSupported(add("If[x < 1, 0, 1]")));
		assertTrue(IntervalFunction.isSupported(add("If[x < 1, x, x + 1]")));
		assertFalse(IntervalFunction.isSupported(add("If[x < 1, x * sin(x), x + 1]")));
		assertFalse(IntervalFunction.isSupported(add("If[x < 1, x, x * sin(x)]")));
		assertFalse(IntervalFunction.isSupported(add("If[x < 1, x/tan(x), x * sin(x)]")));
	}
}