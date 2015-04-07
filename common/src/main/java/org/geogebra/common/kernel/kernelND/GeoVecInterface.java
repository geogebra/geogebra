package org.geogebra.common.kernel.kernelND;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;

/**
 * 
 * @author mathieu
 *
 */
public interface GeoVecInterface  extends ExpressionValue{

	/**
	 * 
	 * @return x value
	 */
	public double getX();
	
	/**
	 * 
	 * @return y value
	 */
	public double getY();
	
	/**
	 * 
	 * @return 3D z value
	 */
	public double getZ();
	
}
