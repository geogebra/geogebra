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

package org.geogebra.common.properties.impl;

import java.util.List;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.GeoGebraColorConstants;

public final class DefaultColorValues {

	/** Bright color values, commonly used for foreground color of objects */
	public static final List<GColor> BRIGHT = List.of(
			GeoGebraColorConstants.NEUTRAL_900,
			GeoGebraColorConstants.MEBIS_ACCENT,
			GeoGebraColorConstants.PURPLE_600,
			GeoGebraColorConstants.GEOGEBRA_OBJECT_BLUE,
			GColor.newColorRGB(0x388C83),
			GColor.newColorRGB(0xFFCC02),
			GColor.newColorRGB(0xE07415),
			GeoGebraColorConstants.GEOGEBRA_OBJECT_RED
	);

	/** Pale color values, commonly used for background color */
	public static final List<GColor> PALE = List.of(
			GColor.WHITE,
			GColor.newColorRGB(0xEADFEE),
			GeoGebraColorConstants.PURPLE_100,
			GColor.newColorRGB(0xCAD5ED),
			GColor.newColorRGB(0xD2E4E2),
			GColor.newColorRGB(0xFFF2CC),
			GColor.newColorRGB(0xF5E0D2),
			GColor.newColorRGB(0xF1D2D8)
	);

	/** Neutral color values, commonly used for text */
	public static final List<GColor> NEUTRAL = List.of(
			GeoGebraColorConstants.NEUTRAL_900,
			GeoGebraColorConstants.NEUTRAL_800,
			GeoGebraColorConstants.NEUTRAL_700,
			GeoGebraColorConstants.NEUTRAL_600,
			GeoGebraColorConstants.NEUTRAL_500,
			GeoGebraColorConstants.NEUTRAL_400,
			GeoGebraColorConstants.NEUTRAL_300,
			GeoGebraColorConstants.NEUTRAL_200
	);

	private DefaultColorValues() {
	}
}
