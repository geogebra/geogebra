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