package org.geogebra.common.jre.gui;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.MyImage;

/** Image interface */
public interface MyImageJre extends MyImage {

	/**
	 * @return SVG as string
	 */
	String getSVG();

	/**
	 * @return whether the implementation is not null
	 */
	boolean hasNonNullImplementation();

	@Override
	default MyImage tintedSVG(GColor color, Runnable onLoad) {
		return null;
	}
}
