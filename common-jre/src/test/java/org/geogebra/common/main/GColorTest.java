package org.geogebra.common.main;

import static org.junit.Assert.assertEquals;

import org.geogebra.common.awt.GColor;
import org.junit.Test;

public class GColorTest {

	@Test
	public void colorParserShouldAcceptRGB() {
		GColor color = GColor.parseHexColor("#123");
		assertEquals(16, color.getRed());
		assertEquals(32, color.getGreen());
		assertEquals(48, color.getBlue());
		assertEquals(255, color.getAlpha());
	}

	@Test
	public void colorParserShouldAcceptRGBA() {
		GColor color = GColor.parseHexColor("#1234");
		assertEquals(16, color.getRed());
		assertEquals(32, color.getGreen());
		assertEquals(48, color.getBlue());
		assertEquals(64, color.getAlpha());
	}

	@Test
	public void colorParserShouldAcceptRRGGBB() {
		GColor color = GColor.parseHexColor("#010203");
		assertEquals(1, color.getRed());
		assertEquals(2, color.getGreen());
		assertEquals(3, color.getBlue());
		assertEquals(255, color.getAlpha());
	}

	@Test
	public void colorParserShouldAcceptRRGGBBAA() {
		GColor color = GColor.parseHexColor("#01020304");
		assertEquals(1, color.getRed());
		assertEquals(2, color.getGreen());
		assertEquals(3, color.getBlue());
		assertEquals(4, color.getAlpha());
	}
}
