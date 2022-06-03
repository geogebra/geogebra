package org.geogebra.common.kernel.interval;


import static org.geogebra.common.kernel.interval.IntervalTest.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.junit.Test;

public class ConditionalSamplerListTest extends BaseUnitTest {

	@Test
	public void testIf() {
		GeoFunction function = add("if(x < 2, 1)");
		ConditionalSamplerList samplers = new ConditionalSamplerList(function,
				interval(-10, 10), 100);
		allEquals(1, samplers.evaluate(-10, 1.9));
		assertEquals(IntervalTupleList.emptyList(), samplers.evaluate(3, 9000));
	}


	static void allEquals(int singleton, IntervalTupleList tuples) {
		int count = tuples.count();
		long filteredCount = tuples.stream().filter(tuple -> tuple.y().almostEqual(
				interval(singleton))).count();
		assertTrue("filtered: " + filteredCount + " all: " + count,
				count > 0 && count == filteredCount);
	}

	@Test
	public void testIfElse() {
		GeoFunction function = add("if(x < 2, 1, 3)");
		ConditionalSamplerList samplers = new ConditionalSamplerList(function,
				interval(-10, 10), 100);
		allEquals(1, samplers.evaluate(-10, 1.9));
		allEquals(3, samplers.evaluate(2, 3));
	}

	@Test
	public void testIfList() {
		GeoFunction function = add("if(x < -2, 0, -2 < x < 2, 1, x > 2, 2)");
		ConditionalSamplerList samplers = new ConditionalSamplerList(function,
				interval(-10, 10), 100);
		allEquals(0, samplers.evaluate(-10, -1.9));
		allEquals(1, samplers.evaluate(-2.1, 1.9));
		allEquals(2, samplers.evaluate(2, 3000));
	}

	@Test
	public void testIfListWithOverlappingConditions() {
		GeoFunction function = add("if(x < 2, 0, -2 < x < 2, 1, x > 2, 2)");
		ConditionalSamplerList samplers = new ConditionalSamplerList(function,
				interval(-10, 10), 100);
		allEquals(0, samplers.evaluate(-10, -1.9));
		allEquals(0, samplers.evaluate(-11, 1.9));
		allEquals(2, samplers.evaluate(2, 3000));
	}
}