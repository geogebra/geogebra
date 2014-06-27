package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.arithmetic.ExpressionValue;

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
