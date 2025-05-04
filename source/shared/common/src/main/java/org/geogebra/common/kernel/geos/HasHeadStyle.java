package org.geogebra.common.kernel.geos;

/**
 * Vector that can be styled.
 */
public interface HasHeadStyle {
	/**
	 * @return vector head style
	 */
	VectorHeadStyle getHeadStyle();

	/**
	 * @param headStyle vector head style
	 */
	void setHeadStyle(VectorHeadStyle headStyle);
}
