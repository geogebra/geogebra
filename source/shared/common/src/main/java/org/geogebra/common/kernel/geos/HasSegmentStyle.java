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
