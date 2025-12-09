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

package org.geogebra.common.euclidian.background;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Types of MOW backgrounds. *
 * 
 * @author laszlo
 *
 */
public enum BackgroundType {
	/** no ruler as background */
	NONE(0),

	/** horizontal lines */
	RULER(1),

	/** small squares */
	SQUARE_SMALL(2),

	/** big squares */
	SQUARE_BIG(3),

	/** Elementary 1/2 background */
	ELEMENTARY12(4),

	/** Elementary 1/2 with house background */
	ELEMENTARY12_HOUSE(5),

	/** Elementary 3/4 background */
	ELEMENTARY34(6),

	/** Music background */
	MUSIC(7),

	/** General SVG as background */
	SVG(8),

	/** Elementary 1/2 with colored background */
	ELEMENTARY12_COLORED(9),

	/** Isometric background */
	ISOMETRIC(10),

	/** Polar background */
	POLAR(11),

	/**
	 * Dots background
	 */
	DOTS(12);

	/**
	 * Ordered list of ruling options used for the settings
	 */
	final public static List<BackgroundType> rulingOptions =
			Collections.unmodifiableList(Arrays.asList(
					BackgroundType.NONE,
					BackgroundType.RULER,
					BackgroundType.SQUARE_SMALL,
					BackgroundType.SQUARE_BIG,
					BackgroundType.ELEMENTARY12_HOUSE,
					BackgroundType.ELEMENTARY12_COLORED,
					BackgroundType.ELEMENTARY12,
					BackgroundType.ELEMENTARY34,
					BackgroundType.MUSIC,
					BackgroundType.ISOMETRIC,
					BackgroundType.POLAR,
					BackgroundType.DOTS));

	private final int value;

	BackgroundType(int value) {
		this.value = value;
	}

	/**
	 * @return the value of the enum.
	 */
	public int value() {
		return value;
	}

	/**
	 * Checks that the given enum value refers to an SVG file.
	 * @return if it is SVG or not.
	 */
	public boolean isSVG() {
		return value() >= ELEMENTARY12.value();
	}

	/**
	 * Converts int to enum
	 * 
	 * @param v
	 *            the int value to convert to.
	 * @return the converted enum.
	 */
	public static BackgroundType fromInt(int v) {
		for (BackgroundType bt : values()) {
			if (bt.value == v) {
				return bt;
			}
		}
		return BackgroundType.NONE;
	}
}
