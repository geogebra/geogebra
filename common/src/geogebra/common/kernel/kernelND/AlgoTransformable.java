package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.Transform;
import geogebra.common.kernel.geos.GeoElement;

/**
 * Interface for algo that handles the transformation of the output, 
 * duplicating itself with transformed inputs
 * @author mathieu
 *
 */
public interface AlgoTransformable {

	/**
	 * Create same algo with transformed inputs.
	 * @param t transformation
	 * @return transformed output
	 */
	public GeoElement[] getTransformedOutput(Transform t);
	
}
