package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.kernelND.GeoVecInterface;

/**
 * tag for VectorValue and Vector3DValue
 * @author mathieu
 *
 */
public interface VectorNDValue extends ExpressionValue{
	
	/**
	 * 
	 * @return vector mode
	 */
	public int getMode();
	
	/**
	 * 
	 * @return vector
	 */
	public GeoVecInterface getVector();

}
