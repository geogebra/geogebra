package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

public interface TextStyle extends GeoElementND {

	/**
	 * @return font style
	 */
	int getFontStyle();

	/**
	 * @return relative font size
	 */
	double getFontSizeMultiplier();

	/**
	 * For buttons
	 * 
	 * @return true if most of these properties are ignored and just size is
	 *         taken into account
	 */
	boolean justFontSize();

}
