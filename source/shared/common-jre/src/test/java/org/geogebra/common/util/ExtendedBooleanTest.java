/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */
 
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
