package org.geogebra.common.kernel.interval;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.BaseUnitTest;
import org.junit.Test;

public class LinearSpaceTest extends BaseUnitTest {

	@Test
	public void testExtendToInt() {
		LinearSpace space = new LinearSpace(0, 10, 10);
		LinearSpace expected = new LinearSpace(0, 18, 18);
		space.extendMax(18);
		assertEquals(expected.values(), space.values());
	}

	@Test
	public void testExtendToReal() {
		LinearSpace space = new LinearSpace(0, 10, 10);
		LinearSpace expected = new LinearSpace(0, 15, 15);
		space.extendMax(14.5);
		assertEquals(expected.values(), space.values());
	}

	@Test
	public void testShrinkToInt() {
		LinearSpace space = new LinearSpace(0, 10, 10);
		LinearSpace expected = new LinearSpace(8, 10, 2);
		space.shrinkMin(8);
		assertEquals(expected.values(), space.values());
	}

	@Test
	public void testShrinkToMinusInt() {
		LinearSpace space = new LinearSpace(0, 10, 10);
		LinearSpace expected = new LinearSpace(-2, 10, 12);
		space.extendMin(-2);
		assertEquals(expected.values(), space.values());
	}

	@Test
	public void testShrinkToReal() {
		LinearSpace space = new LinearSpace(8, 18, 10);
		LinearSpace expected = new LinearSpace(-2, 18, 20);
		space.extendMin(-1.4);
		assertEquals(expected.values(), space.values());
	}
}
