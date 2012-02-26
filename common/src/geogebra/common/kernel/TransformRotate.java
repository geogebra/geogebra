package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoRotate;
import geogebra.common.kernel.algos.AlgoRotatePoint;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;

/**
 * Rotation
 * 
 * @author kondr
 * 
 */
public class TransformRotate extends Transform {

	private GeoPoint2 center;
	private NumberValue angle;

	/**
	 * @param cons construction
	 * @param angle rotation angle
	 */
	public TransformRotate(Construction cons,NumberValue angle) {
		this.angle = angle;
		this.cons = cons;
	}
	
	/**
	 * @param cons construction
	 * @param angle rotation angle
	 * @param center rotation center
	 */
	public TransformRotate(Construction cons,NumberValue angle,GeoPoint2 center) {
		this.angle = angle;
		this.center = center;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = null;
		if (center == null) {
			algo = new AlgoRotate(cons,geo,angle);
		}
		else algo = new AlgoRotatePoint(cons,geo,angle,center);
		return algo;
	}

}
