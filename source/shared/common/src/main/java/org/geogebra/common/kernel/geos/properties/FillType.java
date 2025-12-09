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

package org.geogebra.common.kernel.geos.properties;

/**
 * Fill types of elements
 * 
 * need to be in menu order here
 * 
 * the order here gives the integer for the XML so new ones MUST be added at the
 * end
 * 
 * @author Giulliano Bellucci
 */
public enum FillType {

	/**
	 * Simple fill (color+opacity)
	 * 
	 */
	STANDARD(false),
	/**
	 * Hatched fill
	 */
	HATCH(true),
	/**
	 * Crosshatched fill
	 */
	CROSSHATCHED(true),
	/**
	 * Chessboard fill, upright or diagonal
	 */
	CHESSBOARD(true),
	/**
	 * Dotted fill
	 */
	DOTTED(true),
	/**
	 * Honeycomb fill
	 */
	HONEYCOMB(true),
	/**
	 * Brick fill
	 */
	BRICK(true),
	/**
	 * Weaving fill
	 */
	WEAVING(true),
	/**
	 * Unicode symbols fill
	 */
	SYMBOLS(true),
	/**
	 * Image background
	 */
	IMAGE(false);

	private final boolean hatch;

	FillType(boolean hatch) {
		this.hatch = hatch;
	}

	/**
	 * @return whether this is hatch or something else (image, standard)
	 */
	public boolean isHatch() {
		return hatch;
	}
}