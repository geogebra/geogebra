package org.geogebra.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExtendedBooleanTest {

	@Test
	public void testAnd() {
		assertEquals(ExtendedBoolean.UNKNOWN, ExtendedBoolean.TRUE.and(ExtendedBoolean.UNKNOWN));
		assertEquals(ExtendedBoolean.FALSE, ExtendedBoolean.TRUE.and(ExtendedBoolean.FALSE));
		assertEquals(ExtendedBoolean.UNKNOWN, ExtendedBoolean.UNKNOWN.and(ExtendedBoolean.UNKNOWN));
		assertEquals(ExtendedBoolean.TRUE, ExtendedBoolean.TRUE.and(ExtendedBoolean.TRUE));
	}

	@Test
	public void testOr() {
		assertEquals(ExtendedBoolean.TRUE, ExtendedBoolean.TRUE.or(ExtendedBoolean.UNKNOWN));
		assertEquals(ExtendedBoolean.TRUE, ExtendedBoolean.TRUE.or(ExtendedBoolean.FALSE));
		assertEquals(ExtendedBoolean.UNKNOWN, ExtendedBoolean.UNKNOWN.or(ExtendedBoolean.UNKNOWN));
		assertEquals(ExtendedBoolean.FALSE, ExtendedBoolean.FALSE.or(ExtendedBoolean.FALSE));
	}
}
