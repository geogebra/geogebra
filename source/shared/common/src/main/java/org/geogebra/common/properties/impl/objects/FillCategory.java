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

package org.geogebra.common.properties.impl.objects;

import org.geogebra.common.kernel.geos.properties.FillType;

/**
 * Different categories of fill types a fillable object can have.
 */
public enum FillCategory {
	PATTERN, SYMBOL, IMAGE;

	/**
	 * Matches the given {@link FillType} to its {@code FillCategory}.
	 * @param fillType the fill to find the category for
	 * @return the fill category for the given fill type
	 */
	public static FillCategory fromFillType(FillType fillType) {
		return switch (fillType) {
			case IMAGE -> FillCategory.IMAGE;
			case SYMBOLS -> FillCategory.SYMBOL;
			default -> FillCategory.PATTERN;
		};
	}

	/**
	 * Matches the {@code FillCategory} with the given {@link FillType}.
	 * @param patternFillType to fill type to use for {@link FillCategory#PATTERN}
	 * @return the fill type for this category
	 */
	public FillType toFillType(FillType patternFillType) {
		return switch (this) {
			case PATTERN -> patternFillType;
			case SYMBOL -> FillType.SYMBOLS;
			case IMAGE -> FillType.IMAGE;
		};
	}
}
