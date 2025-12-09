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

package org.geogebra.common.main.color;

import java.util.Arrays;
import java.util.List;

import org.geogebra.common.awt.GColor;

/**
 * Colors for construction elements.
 */
public final class GeoColorValues {
	
	/**
	 * @return default colors.
	 * @apiNote The returned list may contain {@code null} values. These {@code null} values
	 * are "markers" that indicate indices where a plus button should be shown in the UI.
	 */
	public static List<GColor> values() {
		return Arrays.asList(
				GColor.newColorRGB(0x1C1C1F),
				GColor.newColorRGB(0x6E6D73),
				GColor.newColorRGB(0xB4B3BA),
				GColor.newColorRGB(0xD1D0D6),
				GColor.newColorRGB(0xF3F2F7),
				GColor.WHITE,
				null,
				GColor.newColorRGB(0x723B86),
				GColor.newColorRGB(0x5145A8),
				GColor.newColorRGB(0x143E92),
				GColor.newColorRGB(0x006758),
				GColor.newColorRGB(0xF1C232),
				GColor.newColorRGB(0xC75000),
				GColor.newColorRGB(0xB00020),
				GColor.newColorRGB(0x975FA8),
				GColor.newColorRGB(0x6557D2),
				GColor.newColorRGB(0x1565C0),
				GColor.newColorRGB(0x388C83),
				GColor.newColorRGB(0xFFCC02),
				GColor.newColorRGB(0xE07415),
				GColor.newColorRGB(0xD32F2F),
				GColor.newColorRGB(0xEADFEE),
				GColor.newColorRGB(0xF3F0FF),
				GColor.newColorRGB(0xCAD5ED),
				GColor.newColorRGB(0xD2E4E2),
				GColor.newColorRGB(0xFFF2CC),
				GColor.newColorRGB(0xF5E0D2),
				GColor.newColorRGB(0xF1D2D8));
	}

	private GeoColorValues() {
		// utility class
	}
}
