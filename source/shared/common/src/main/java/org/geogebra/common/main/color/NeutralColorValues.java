package org.geogebra.common.main.color;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GColor;

public final class NeutralColorValues {

	private NeutralColorValues() {
		// utility class
	}

	/**
	 * @return neutral colors
	 */
	public static List<GColor> values() {
		return Arrays.asList(
				GColor.newColorRGB(0x1C1C1F),
				GColor.newColorRGB(0x2F2F33),
				GColor.newColorRGB(0x6E6D73),
				GColor.newColorRGB(0x85848A),
				GColor.newColorRGB(0xB4B3BA),
				GColor.newColorRGB(0xD1D0D6),
				GColor.newColorRGB(0xE6E6EB),
				GColor.newColorRGB(0xF3F2F7),
				null);
	}
}
