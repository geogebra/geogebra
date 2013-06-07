package geogebra.common.kernel.kernelND;

import geogebra.common.kernel.Matrix.Coords;

/**
 * Interface for geos that can be evaluate as 3D curve t -> (x,y,z)
 * @author mathieu
 *
 */
public interface CurveEvaluable3D {

	public double getMaxParameter();

	public double getMinParameter();

	public Coords evaluateCurve3D(double t);


}
