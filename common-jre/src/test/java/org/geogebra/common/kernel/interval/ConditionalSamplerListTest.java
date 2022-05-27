package org.geogebra.common.kernel.interval;


import static org.geogebra.common.kernel.interval.IfFunctionSamplerTest.allEquals;
import static org.geogebra.common.kernel.interval.IntervalTest.interval;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class ConditionalSamplerListTest extends BaseUnitTest {

	@Test
	public void testIfList() {
		GeoFunction function = add("if(x < 2, 1, x < 3, 3)");
		ConditionalSamplerList samplers = new ConditionalSamplerList(function,
				interval(-10, 10), 100);
		allEquals(1, samplers.evaluateBetween(-10, 2));
		allEquals(2, samplers.evaluateBetween(2, 3));
	}
}