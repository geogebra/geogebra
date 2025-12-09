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

package org.geogebra.common.main;

import static org.geogebra.common.awt.GColor.BLACK;
import static org.geogebra.test.OrderingComparison.greaterThan;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.kernel.AutoColor;
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

	@Test
	public void testLuminance() {
		assertEquals(1.0, GColor.WHITE.getLuminance(), 0.01);
		assertEquals(0, GColor.BLACK.getLuminance(), 0.01);
		assertEquals(0.0722, GColor.BLUE.getLuminance(), 0.01);
		assertEquals(0.2126, GColor.RED.getLuminance(), 0.01);
		assertEquals(0.7152, GColor.GREEN.getLuminance(), 0.01);
	}

	@Test
	public void testContrast() {
		assertEquals(21.0, GColor.WHITE.getContrast(GColor.BLACK), 0.01);
		assertEquals(21.0, GColor.BLACK.getContrast(GColor.WHITE), 0.01);
		for (int i = 0; i < 10; i++) {
			GColor objColor = AutoColor.CURVES.getNext(true);
			assertThat(objColor.getContrast(GColor.WHITE), greaterThan(3.0));
		}
	}

	@Test
	public void testContrastAll() {
		double contrast = 10;
		GColor leastContrast = BLACK;
		List<String> lines = new ArrayList<>(List.of("<style>",
				"div {",
				"height:30px;",
				"width:200px;",
				"border: 2px solid;",
				"display: flex;",
				"align-items: center;",
				"justify-content: center;",
				"margin: 10px;",
				"border-radius: 4px;",
				"}",

				"body {",
				"display: flex;",
				"flex-wrap: wrap;",
				"width: 500px;",
				"}",
				"</style>",
				"<body>"));
		for (int r = 0; r < 255; r += 20) {
			for (int g = 0; g < 255; g += 20) {
				for (int b = 0; b < 255; b += 20) {
					GColor base = GColor.newColor(r, g, b);
					GColor border = GColor.getBorderColorFrom(base);
					double contrast1 = base.getContrast(border);
					lines.add("<div style=\"background-color:" + base + ";border-color:" + border
										+ "\">" + contrast1 + "</div>");
					if (contrast > contrast1 && BLACK.getContrast(base) > 2.5) {
						contrast = contrast1;
						leastContrast = base;
					}
				}
			}
		}
		assertTrue(leastContrast + "\n"
				+ GColor.getBorderColorFrom(leastContrast).toString() + ": " + contrast,
				contrast >= 2);
		assertFalse(String.join("\n", lines).contains("NaN"));
		// This test can be used to generate a report like this:
		//Files.writeString(Path.of("build/divs.html"), String.join("\n", lines));
	}
}
