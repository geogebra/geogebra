package org.geogebra.common.kernel.implicit;

/**
 * 
 * @author GSoCImplicit2015
 *
 */
class Consts {
	/**
	 * Empty Grid
	 */
	public static final int EMPTY = 0x01;

	/**
	 * Ambiguous Grid
	 */
	public static final int AMBIGUOUS = 0x02;

	/**
	 * Singular Grid
	 */
	public static final int SINGULAR = 0x04;
	/**
	 * Finished Grid
	 */
	public static final int FINISHED = 0x08;
	/**
	 * Valid Grid
	 */
	public static final int VALID = 0x10;
	/**
	 * Neighbor grid info
	 */
	public static final int NEIGHBOR = 0x20;
	/**
	 * Top side of the Grid
	 */
	public static final int TOP = 0x00;
	/**
	 * Right side of grid
	 */
	public static final int RIGHT = 0x01;
	/**
	 * Bottom side of grid
	 */
	public static final int BOTTOM = 0x02;
	/**
	 * Left side of grid
	 */
	public static final int LEFT = 0x03;
}
