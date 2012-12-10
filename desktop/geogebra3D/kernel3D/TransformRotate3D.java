package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.TransformRotate;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * 3D rotations
 * @author mathieu
 *
 */
public class TransformRotate3D extends TransformRotate{

	private GeoDirectionND orientation;
	
	/**
	 * constructor
	 * @param cons construction
	 * @param angle rotation angle
	 * @param center center
	 * @param orientation orientation
	 */
	public TransformRotate3D(Construction cons, NumberValue angle,
			GeoPointND center, GeoDirectionND orientation) {
		super(cons, angle, center);
		this.orientation = orientation;

	}
	
	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = null;
		algo = new AlgoRotate3DPointOrientation(cons, geo,angle, center, orientation);
		return algo;
	}

}
