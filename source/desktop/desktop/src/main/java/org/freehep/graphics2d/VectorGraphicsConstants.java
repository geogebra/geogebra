// University of Santa Cruz, California, USA and
// CERN, Geneva, Switzerland, Copyright (c) 2000
package org.freehep.graphics2d;

/**
 * This interface defines useful constants for users of the VectorGraphics
 * interface.
 * 
 * @author Charles Loomis
 * @version $Id: VectorGraphicsConstants.java,v 1.4 2009-08-17 21:44:44 murkle
 *          Exp $
 */
public interface VectorGraphicsConstants {

	// //
	// Symbol definitions
	// //

	/**
	 * Vertical line (|) symbol.
	 */
	final public static int SYMBOL_VLINE = 0;

	/**
	 * Horizontal line (-) symbol.
	 */
	final public static int SYMBOL_HLINE = 1;

	/**
	 * Plus-shaped (+) symbol.
	 */
	final public static int SYMBOL_PLUS = 2;

	/**
	 * An x-shaped (x) symbol.
	 */
	final public static int SYMBOL_CROSS = 3;

	/**
	 * An eight-point star created by combining the plus and cross symbols.
	 */
	final public static int SYMBOL_STAR = 4;

	/**
	 * An open circle (o) symbol.
	 */
	final public static int SYMBOL_CIRCLE = 5;

	/**
	 * An open square symbol.
	 */
	final public static int SYMBOL_BOX = 6;

	/**
	 * An open equilateral triangle pointing up.
	 */
	final public static int SYMBOL_UP_TRIANGLE = 7;

	/**
	 * An open equilateral triangle pointing down.
	 */
	final public static int SYMBOL_DN_TRIANGLE = 8;

	/**
	 * An open square symbol rotated by 45 degrees.
	 */
	final public static int SYMBOL_DIAMOND = 9;

	/**
	 * The number of defined symbols. Used in implementations of the
	 * VectorGraphics interfaces.
	 */
	final public static int NUMBER_OF_SYMBOLS = 10;

	// //
	// Text alignment definitions
	// //

	/**
	 * Constant indicating that a string should be aligned vertically with the
	 * baseline of the text. This is the default in drawString calls which do
	 * not specify an alignment.
	 */
	public static final int TEXT_BASELINE = 0;

	/**
	 * Constant indicating that a string should be aligned vertically with the
	 * top of the text.
	 */
	public static final int TEXT_TOP = 1;

	/**
	 * Constant indicating that a string should be aligned vertically with the
	 * bottom of the text.
	 */
	public static final int TEXT_BOTTOM = 3;

	/**
	 * Constant indicating that a string should be aligned by the center. This
	 * is used for both horizontal and vertical alignment.
	 */
	public static final int TEXT_CENTER = 2;

	/**
	 * Constant indicating that a string should be aligned horizontally with the
	 * left side of the text. This is the default for drawString calls which do
	 * not specify an alignment.
	 */
	public static final int TEXT_LEFT = 1;

	/**
	 * Constant indicating that the string should be aligned horizontally with
	 * the right side of the text.
	 */
	public static final int TEXT_RIGHT = 3;

	/**
	 * Constant indicating the maximum number of vertical alignments. Used in
	 * implementation of the VectorGraphics interfaces.
	 */
	public static final int NUMBER_OF_VERTICAL_ALIGNMENTS = 4;

	/**
	 * Constant indicating the maximum number of horizontal alignments. Used in
	 * implementation of the VectorGraphics interfaces.
	 */
	public static final int NUMBER_OF_HORIZ_ALIGNMENTS = 4;

}
