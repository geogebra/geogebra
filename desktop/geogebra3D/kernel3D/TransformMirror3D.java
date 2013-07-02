package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.TransformMirror;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoLineND;
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
	 * @param point mirror point
	 * 
	 */
	public TransformMirror3D(Construction cons, GeoPointND point) {
		super(cons, (GeoElement) point);

	}
	
	/**
	 * constructor
	 * @param cons construction
	 * @param line mirror line
	 * 
	 */
	public TransformMirror3D(Construction cons, GeoLineND line) {
		super(cons, (GeoElement) line);

	}
	
	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = null;
		if (mirror.isGeoPoint()) 
			algo = new AlgoMirror3D(cons, geo, (GeoPointND) mirror);
		else if(mirror.isGeoLine())
			algo = new AlgoMirror3D(cons, geo, (GeoLineND) mirror);
		return algo;
	}

}
