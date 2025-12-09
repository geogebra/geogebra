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

package org.geogebra.keyboard.web.factory.model.inputbox.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CursiveTest {
	private static final String X = "\uD835\uDC65";
	private static final String XX = "\uD835\uDC65\uD835\uDC65";
	private static final String XXX = "\uD835\uDC65\uD835\uDC65\uD835\uDC65";

	@Test
	public void testCursiveCaption() {
		assertEquals(X, Cursive.getCursiveCaption("x"));
		assertEquals(XX, Cursive.getCursiveCaption("xx"));
		assertEquals(XXX, Cursive.getCursiveCaption("xxx"));
		assertEquals(XXX, Cursive.getCursiveCaption("xxxlongvariable"));
	}

	@Test
	public void testCursiveCaptionWithIndex() {
		assertEquals(X + "_1234", Cursive.getCursiveCaption("x_123456"));
		assertEquals(XX + "_12", Cursive.getCursiveCaption("xx_123456"));
		assertEquals(XXX, Cursive.getCursiveCaption("xxx_123456"));
	}
}