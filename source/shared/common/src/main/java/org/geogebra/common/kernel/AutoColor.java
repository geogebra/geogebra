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

package org.geogebra.common.kernel;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.main.GeoGebraColorConstants;

/**
 * 
 * Helper for auto coloring geos
 *
 */
public enum AutoColor {
	/** curves, functions (classic app) */
	CURVES(new GColor[] { GeoGebraColorConstants.GGB_GREEN,
			GeoGebraColorConstants.GGB_RED, GColor.BLUE,
			GeoGebraColorConstants.GGB_ORANGE,
			GeoGebraColorConstants.GGB_CLASSIC_PURPLE, GeoGebraColorConstants.GGB_GRAY,
			GeoGebraColorConstants.GGB_BROWN }),
	/** curves, function (graphing) */
	CURVES_GRAPHING(new GColor[] { GeoGebraColorConstants.GEOGEBRA_OBJECT_GREEN,
			GeoGebraColorConstants.GEOGEBRA_OBJECT_BLUE,
			GeoGebraColorConstants.GEOGEBRA_OBJECT_RED,
			GeoGebraColorConstants.GGB_ORANGE,
			GeoGebraColorConstants.PURPLE_600,
			GeoGebraColorConstants.GEOGEBRA_OBJECT_GREY }),
	/** surfaces */
	SURFACES(new GColor[] { GeoGebraColorConstants.MEBIS_ACCENT,
			GColor.PURPLE_A400,
			GeoGebraColorConstants.GEOGEBRA_OBJECT_PINK,
			GeoGebraColorConstants.PURPLE_600,
			GeoGebraColorConstants.GGB_ORANGE });

	private final GColor[] sequence;
	private int index;

	private AutoColor(GColor[] sequence) {
		this.sequence = sequence;
		index = 0;
	}

	/**
	 * 
	 * @param shiftIndex
	 *            if index to be shifted
	 * @return next color
	 */
	public GColor getNext(boolean shiftIndex) {
		GColor color = sequence[index];
		if (shiftIndex) {
			index = (index + 1) % sequence.length;
		}
		return color;
	}
}
