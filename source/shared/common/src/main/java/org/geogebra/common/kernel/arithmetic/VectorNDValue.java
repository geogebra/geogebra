package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.kernelND.GeoVecInterface;

/**
 * tag for VectorValue and Vector3DValue
 * 
 * @author mathieu
 *
 */
public interface VectorNDValue extends ExpressionValue {

	/**
	 * @return string mode: Kernel.COORD_COMPLEX, COORD_CARTESIAN etc.
	 */
	public int getToStringMode();

	/**
	 * @return dimension
	 */
	public int getDimension();

	/**
	 * 
	 * @return vector
	 */
	public GeoVecInterface getVector();

	/**
	 * @return array of coordinates
	 */
	public double[] getPointAsDouble();

	/**
	 * @param mode
	 *            one of Kernel.COORD_* constants
	 */
	public void setMode(int mode);

}
