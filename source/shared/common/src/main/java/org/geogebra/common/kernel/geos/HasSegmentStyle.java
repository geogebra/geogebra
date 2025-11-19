package org.geogebra.common.kernel.geos;

import org.geogebra.common.io.XMLStringBuilder;
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
	default void appendStartEndStyle(XMLStringBuilder sb) {
		sb.startTag("startStyle").attr("val", getStartStyle()).endTag();
		sb.startTag("endStyle").attr("val", getEndStyle()).endTag();
	}
}
