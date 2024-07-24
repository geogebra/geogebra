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

	private GColor[] sequence;
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
