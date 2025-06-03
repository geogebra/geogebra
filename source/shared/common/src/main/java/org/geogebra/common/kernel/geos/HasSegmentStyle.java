package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.kernelND.GeoElementND;

public interface HasSegmentStyle extends GeoElementND {

	/**
	 * @param startStyle - segment start style
	 */
	void setStartStyle(SegmentStyle startStyle);

	/**
	 * @param endStyle - segment end style
	 */
	void setEndStyle(SegmentStyle endStyle);

	/**
	 * @return segment start style
	 */
	SegmentStyle getStartStyle();

	/**
	 * @return segment end style
	 */
	SegmentStyle getEndStyle();

	/**
	 * @return whether at least one style is non-default
	 */
	default boolean hasStyledEndpoint() {
		return !(getStartStyle().isDefault() && getEndStyle().isDefault());
	}

	/**
	 * Append style information to XML
	 * @param sb XML builder
	 */
	default void appendStartEndStyle(StringBuilder sb) {
		sb.append("\t<startStyle val=\"");
		sb.append(getStartStyle().toString());
		sb.append("\"/>\n");

		sb.append("\t<endStyle val=\"");
		sb.append(getEndStyle().toString());
		sb.append("\"/>\n");
	}
}
