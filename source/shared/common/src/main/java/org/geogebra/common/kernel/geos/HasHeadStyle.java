package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

/**
 * Vector that can be styled.
 */
public interface HasHeadStyle extends GeoElementND {
	/**
	 * @return vector head style
	 */
	VectorHeadStyle getHeadStyle();

	/**
	 * @param headStyle vector head style
	 */
	void setHeadStyle(VectorHeadStyle headStyle);
}
