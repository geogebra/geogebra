package org.geogebra.common.euclidian.plot.interval;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.kernel.interval.IntervalTupleList;
import org.geogebra.common.kernel.interval.SamplerTest;
import org.junit.Ignore;
import org.junit.Test;

public class IntervalSampleAnalizerTest extends SamplerTest {

	private final IntervalSampleAnalizer analizer = new IntervalSampleAnalizer();

	@Test
	public void testHasValidData() {
		withHiResFunction("0/(0/x)");
		assertTrue(analizer.hasValidData());
	}

	@Ignore
	@Test
	public void testAllWhole() {
		// TODO: fix me!
		withHiResFunction("10/(0/x)");
		assertTrue(analizer.isAllWhole());
	}

	private void withHiResFunction(String description) {
		IntervalTupleList tuples = hiResFunction(description);
		analizer.setTuples(tuples);
	}

	@Test
	public void testDescendingAbsX() {
		withFunction("abs(x)");
		assertTrue(analizer.isDescendingFrom(0));
		assertTrue(analizer.isDescendingFrom(10));
		assertTrue(analizer.isDescendingFrom(15));
		assertTrue(analizer.isDescendingFrom(30));
		assertFalse(analizer.isDescendingFrom(50));
		assertFalse(analizer.isDescendingFrom(60));
		assertFalse(analizer.isDescendingFrom(70));
		assertFalse(analizer.isDescendingFrom(99));
		assertFalse(analizer.isDescendingFrom(100));
	}

	@Test
	public void testDivergentTanX() {
		withFunction("tan(x)");
		assertTrue(analizer.isDivergentAt(10));
	}

	@Test
	public void testNoDivergent() {
		withFunction("(x^(1/9))^-2");
		assertFalse(analizer.isDivergentAt(50));
	}

	private void withFunction(String description) {
		IntervalTupleList tuples = functionValues(description, -10, 10, -10, 10);
		analizer.setTuples(tuples);
	}
}