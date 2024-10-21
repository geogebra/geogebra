package org.geogebra.common.kernel.interval.node;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.plugin.Operation;
import org.junit.Test;

public class IntervalOperationTest {

	private final IntervalOperationSupport support = new IntervalOperationSupport();
	
	@Test
	public void testIsSupported() {
		assertTrue(support.isSupported(Operation.ABS));
		assertTrue(support.isSupported(Operation.ARCCOS));
		assertTrue(support.isSupported(Operation.ARCTAN));
		assertTrue(support.isSupported(Operation.COS));
		assertTrue(support.isSupported(Operation.COSH));
		assertTrue(support.isSupported(Operation.COT));
		assertTrue(support.isSupported(Operation.CSC));
		assertTrue(support.isSupported(Operation.DIVIDE));
		assertTrue(support.isSupported(Operation.EXP));
		assertTrue(support.isSupported(Operation.LOG));
		assertTrue(support.isSupported(Operation.LOG2));
		assertTrue(support.isSupported(Operation.LOG10));
		assertTrue(support.isSupported(Operation.MINUS));
		assertTrue(support.isSupported(Operation.MULTIPLY));
		assertTrue(support.isSupported(Operation.NROOT));
		assertTrue(support.isSupported(Operation.PLUS));
		assertTrue(support.isSupported(Operation.POWER));
		assertTrue(support.isSupported(Operation.SEC));
		assertTrue(support.isSupported(Operation.SIN));
		assertTrue(support.isSupported(Operation.SINH));
		assertTrue(support.isSupported(Operation.SQRT));
		assertTrue(support.isSupported(Operation.TAN));
		assertTrue(support.isSupported(Operation.TANH));
		assertTrue(support.isSupported(Operation.LOGB));
		assertTrue(support.isSupported(Operation.INVISIBLE_PLUS));
	}

	@Test
	public void testHasNotEquivalent() {
		assertFalse(support.isSupported(Operation.ALT));
		assertFalse(support.isSupported(Operation.DIRAC));
		assertFalse(support.isSupported(Operation.GAMMA));
		assertFalse(support.isSupported(Operation.HEAVISIDE));
	}
}