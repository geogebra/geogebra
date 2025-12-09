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

public final class BackgroundColorValues {
	/**
	 * @return neutral colors
	 * @apiNote The returned list may contain {@code null} values. These {@code null} values
	 * are "markers" that indicate indices where a plus button should be shown in the UI.
	 */
	public static List<GColor> values() {
		return Arrays.asList(
				GColor.WHITE,
				GColor.newColorRGB(0xbEADFEE),
				GColor.newColorRGB(0xF3F0FF),
				GColor.newColorRGB(0xCAD5ED),
				GColor.newColorRGB(0xD2E4E2),
				GColor.newColorRGB(0xFFF2CC),
				GColor.newColorRGB(0xF5E0D2),
				GColor.newColorRGB(0xF1D2D8),
				null);
	}

	private BackgroundColorValues() {
		// utility class
	}
}
