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

package org.geogebra.common.javax.swing;

/**
 * SwingConstants
 * 
 * @see <a href="http://google.com">http://docs.oracle.com/javase/7/docs/api/
 *      constant-values.html#javax.swing.SwingConstants.BOTTOM</a>
 *
 */
public interface SwingConstants {
	/**
	 * The central position in an area. Used for both compass-direction
	 * constants (NORTH, etc.) and box-orientation constants (TOP, etc.).
	 */
	int CENTER = 0;

	//
	// Box-orientation constant used to specify locations in a box.
	//
	/**
	 * Box-orientation constant used to specify the top of a box.
	 */
	int TOP = 1;
	/**
	 * Box-orientation constant used to specify the left side of a box.
	 */
	int LEFT = 2;
	/**
	 * Box-orientation constant used to specify the bottom of a box.
	 */
	int BOTTOM = 3;
	/**
	 * Box-orientation constant used to specify the right side of a box.
	 */
	int RIGHT = 4;

	//
	// Compass-direction constants used to specify a position.
	//
	/**
	 * Compass-direction North (up).
	 */
	int NORTH = 1;
	/**
	 * Compass-direction north-east (upper right).
	 */
	int NORTH_EAST = 2;
	/**
	 * Compass-direction east (right).
	 */
	int EAST = 3;
	/**
	 * Compass-direction south-east (lower right).
	 */
	int SOUTH_EAST = 4;
	/**
	 * Compass-direction south (down).
	 */
	int SOUTH = 5;
	/**
	 * Compass-direction south-west (lower left).
	 */
	int SOUTH_WEST = 6;
	/**
	 * Compass-direction west (left).
	 */
	int WEST = 7;
	/**
	 * Compass-direction north west (upper left).
	 */
	int NORTH_WEST = 8;

	/** See JSplitPane.HORIZONTAL_SPLIT */
	int HORIZONTAL_SPLIT = 1;
	/** See JSplitPane.VERTICAL_SPLIT */
	int VERTICAL_SPLIT = 0;
	//
	// Constants for orientation support, since some languages are
	// left-to-right oriented and some are right-to-left oriented.
	// This orientation is currently used by buttons and labels.
	//
	/**
	 * Identifies the leading edge of text for use with left-to-right and
	 * right-to-left languages. Used by buttons and labels.
	 */
	int LEADING = 10;
	/**
	 * Identifies the trailing edge of text for use with left-to-right and
	 * right-to-left languages. Used by buttons and labels.
	 */
	int TRAILING = 11;
	/**
	 * Identifies the next direction in a sequence.
	 *
	 * @since 1.4
	 */
	int NEXT = 12;

	/**
	 * Identifies the previous direction in a sequence.
	 *
	 * @since 1.4
	 */
	int PREVIOUS = 13;
}
