package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.TransformMirror;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * 3D rotations
 * @author mathieu
 *
 */
public class TransformMirror3D extends TransformMirror{

	
	/**
	 * constructor
	 * @param cons construction
	 * @param point point
	 * 
	 */
	public TransformMirror3D(Construction cons, GeoPointND point) {
		super(cons, (GeoElement) point);

	}
	
	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = null;
		if (mirror.isGeoPoint()) 
			algo = new AlgoMirror3D(cons, geo, (GeoPointND) mirror);
		return algo;
	}

}
