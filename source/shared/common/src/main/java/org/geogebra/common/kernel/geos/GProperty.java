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

/** properties for View.updateStyle */
public enum GProperty {
	/** font */
	FONT,
	/** foreground color */
	COLOR,
	/** startpoint */
	POSITION,
	/** caption */
	CAPTION,
	/** multiple properties changed */
	COMBINED,
	/** angle interval */
	ANGLE_INTERVAL,
	/** background color */
	COLOR_BG,
	/** line thickness / type */
	LINE_STYLE,
	/** point size / type */
	POINT_STYLE,
	/** visibility */
	VISIBLE,
	/** layer */
	LAYER,
	/** angle style */
	ANGLE_STYLE,
	/** label type */
	LABEL_STYLE,
	/** textfield length */
	LENGTH,
	/** hatching */
	HATCHING,
	/** highlight */
	HIGHLIGHT,
	/** Selection for text/table */
	TEXT_SELECTION,
	/** Wrapping, border etc. */
	TABLE_STYLE,
	/** Angle/segment decoration */
	DECORATION,
}
