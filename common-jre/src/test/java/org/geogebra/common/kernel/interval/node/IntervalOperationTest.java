package org.geogebra.common.kernel.interval.node;

import static org.junit.Assert.assertTrue;

import org.geogebra.common.plugin.Operation;
import org.junit.Test;

public class IntervalOperationTest {

	@Test
	public void testHasEquivalent() {
		assertTrue(IntervalOperation.hasEquivalent(Operation.ABS));
		assertTrue(IntervalOperation.hasEquivalent(Operation.ARCCOS));
		assertTrue(IntervalOperation.hasEquivalent(Operation.ARCTAN));
		assertTrue(IntervalOperation.hasEquivalent(Operation.COS));
		assertTrue(IntervalOperation.hasEquivalent(Operation.COSH));
		assertTrue(IntervalOperation.hasEquivalent(Operation.COT));
		assertTrue(IntervalOperation.hasEquivalent(Operation.CSC));
		assertTrue(IntervalOperation.hasEquivalent(Operation.DIVIDE));
		assertTrue(IntervalOperation.hasEquivalent(Operation.EXP));
		assertTrue(IntervalOperation.hasEquivalent(Operation.LOG));
		assertTrue(IntervalOperation.hasEquivalent(Operation.LOG2));
		assertTrue(IntervalOperation.hasEquivalent(Operation.LOG10));
		assertTrue(IntervalOperation.hasEquivalent(Operation.MINUS));
		assertTrue(IntervalOperation.hasEquivalent(Operation.MULTIPLY));
		assertTrue(IntervalOperation.hasEquivalent(Operation.NROOT));
		assertTrue(IntervalOperation.hasEquivalent(Operation.PLUS));
		assertTrue(IntervalOperation.hasEquivalent(Operation.POWER));
		assertTrue(IntervalOperation.hasEquivalent(Operation.SEC));
		assertTrue(IntervalOperation.hasEquivalent(Operation.SIN));
		assertTrue(IntervalOperation.hasEquivalent(Operation.SINH));
		assertTrue(IntervalOperation.hasEquivalent(Operation.SQRT));
		assertTrue(IntervalOperation.hasEquivalent(Operation.TAN));
		assertTrue(IntervalOperation.hasEquivalent(Operation.TANH));
	}

	@Test
	public void testHasNotEquivalent() {
		IntervalOperation.hasEquivalent(Operation.ALT);
		IntervalOperation.hasEquivalent(Operation.GAMMA);
	}
}