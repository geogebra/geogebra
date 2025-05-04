package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.geos.properties.VerticalAlignment;

/**
 * Object that has vertical alignment.
 */
public interface HasVerticalAlignment {
	/**
	 * @return vertical alignment
	 */
	VerticalAlignment getVerticalAlignment();

	/**
	 * @param valign vertical alignment
	 */
	void setVerticalAlignment(VerticalAlignment valign);
}
