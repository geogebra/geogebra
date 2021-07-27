package org.geogebra.common.jre.gui;

import org.geogebra.common.awt.MyImage;

/** Image interface */
public interface MyImageJre extends MyImage {

	/**
	 * @return SVG as string
	 */
	public String getSVG();

	/**
	 * @return whether the implementation is not null
	 */
	public boolean hasNonNullImplementation();
}
