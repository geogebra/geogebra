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
	 * 
	 * @return vector mode
	 */
	public int getMode();
	
	/**
	 * @return dimension
	 */
	public int getDimension();

	/**
	 * 
	 * @return vector
	 */
	public GeoVecInterface getVector();

}
