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

package org.geogebra.common.gui.colorpicker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geogebra.common.awt.GColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class HsvColorTest {
	private static final double EPS = 1e-9;
	private static final GColor GRAY = GColor.newColorRGB(0x808080);

	@Test
	void toGColorConvertsPrimaries() {
		assertEquals(GColor.newColorRGB(0xFF0000), new HsvColor(0, 1, 1).toGColor());
		assertEquals(GColor.newColorRGB(0x00FF00), new HsvColor(120, 1, 1).toGColor());
		assertEquals(GColor.newColorRGB(0x0000FF), new HsvColor(240, 1, 1).toGColor());
	}

	@ParameterizedTest
	@ValueSource(ints = {0x1C1C1F, 0x6557D2, 0xFFCC02, 0x670000, 0xFFFFFF, 0x000000, 0x9E4848})
	void roundTripPreservesRgb(int rgb) {
		GColor color = GColor.newColorRGB(rgb);
		assertEquals(color, HsvColor.fromGColor(color, null).toGColor());
	}

	@ParameterizedTest
	@CsvSource({
		"-30, 1.5, -0.5, 330, 1, 0",
		"360, 1, 1, 0, 1, 1",
		"-360, 1, 1, 0, 1, 1", // -360 % 360 is -0.0
		"-1e-14, 1, 1, 0, 1, 1", // would round up to 360
		"NaN, NaN, NaN, 0, 0, 0"
	})
	void constructorNormalizesComponents(
			double hue,
			double saturation,
			double value,
			double expectedHue,
			double expectedSaturation,
			double expectedValue) {
		HsvColor expected = new HsvColor(expectedHue, expectedSaturation, expectedValue);
		HsvColor actual = new HsvColor(hue, saturation, value);
		assertEquals(expected, actual);
		assertEquals(expected.hashCode(), actual.hashCode());
	}

	@Test
	void fromGColorTakesUndefinedComponentsFromPrevious() {
		HsvColor previous = new HsvColor(200, 0.5, 0.8);
		assertEquals(new HsvColor(200, 0, 128 / 255.0), HsvColor.fromGColor(GRAY, previous));
		assertEquals(new HsvColor(200, 0.5, 0), HsvColor.fromGColor(GColor.BLACK, previous));
		assertEquals(0, HsvColor.fromGColor(GRAY, null).getHue(), EPS);
	}

	@Test
	void toHexIsUppercaseWithHash() {
		assertEquals(
				"#1C1C1F", HsvColor.fromGColor(GColor.newColorRGB(0x1C1C1F), null).toHex());
	}
}
