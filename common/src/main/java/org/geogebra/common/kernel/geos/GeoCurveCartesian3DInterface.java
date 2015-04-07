package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Matrix.Coords;


/**
 * Interface for GeoElements that can be evaluated as a 3D curve
 * 
 * @author matthieu
 *
 */
public interface GeoCurveCartesian3DInterface {
	
	/** return point at parameter t
	 * @param t parameter
	 * @return point at parameter t
	 */
	public Coords evaluateCurve(double t);
	
	
	/** return tangent at parameter t
	 * @param t parameter
	 * @return tangent at parameter t
	 */
	public Coords evaluateTangent(double t);
	
	/** return curvature at parameter t
	 * @param t parameter
	 * @return curvature at parameter t
	 */
	public double evaluateCurvature(double t);

}
