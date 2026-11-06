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
 * Colors for sticky note.
 */
public final class StickyNoteColorValues {

	/**
	 * @return default colors for {@link org.geogebra.common.kernel.geos.GeoStickyNote}.
	 */
	public static List<GColor> values() {
		return Arrays.asList(
				GColor.newColorRGB(0xC19FCB),
				GColor.newColorRGB(0xB9B1FC),
				GColor.newColorRGB(0x80D8FF),
				GColor.newColorRGB(0x8DEBE0),
				GColor.newColorRGB(0xFFD54F),
				GColor.newColorRGB(0xFF8C70),
				GColor.newColorRGB(0xFF90C3),
				GColor.newColorRGB(0xEADFEE),
				GColor.newColorRGB(0xF3F0FF),
				GColor.newColorRGB(0xD4F2FF),
				GColor.newColorRGB(0xD5F7F3),
				GColor.newColorRGB(0xFFF2CC),
				GColor.newColorRGB(0xF5E0D2),
				GColor.newColorRGB(0xF7E5E9));
	}

	private StickyNoteColorValues() {
		// utility class
	}
}
