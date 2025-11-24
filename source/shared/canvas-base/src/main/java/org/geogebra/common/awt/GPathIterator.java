package org.geogebra.common.awt;

/**
 * Path iterator.
 */
public interface GPathIterator {

	int WIND_EVEN_ODD = 0;
	int WIND_NON_ZERO = 1;

	int SEG_MOVETO = 0;
	int SEG_LINETO = 1;
	int SEG_QUADTO = 2;
	int SEG_CUBICTO = 3;
	int SEG_CLOSE = 4;

	/**
	 * @return the winding rule
	 */
	int getWindingRule();

	/**
	 * @return true iff there are no segments left
	 */
	boolean isDone();

	/**
	 * Go to next segment.
	 */
	void next();

	// public int currentSegment(float[] coords);

	/**
	 * @param coords output array for coordinates
	 * @return segment type
	 */
	int currentSegment(double[] coords);

}
