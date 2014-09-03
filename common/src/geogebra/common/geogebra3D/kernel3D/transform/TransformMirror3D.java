package geogebra.common.geogebra3D.kernel3D.transform;

import geogebra.common.geogebra3D.kernel3D.algos.AlgoMirror3D;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.TransformMirror;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoCoordSys2D;
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
	
	/**
	 * constructor
	 * @param cons construction
	 * @param plane mirror plane
	 * 
	 */
	public TransformMirror3D(Construction cons, GeoCoordSys2D plane) {
		super(cons, (GeoElement) plane);

	}
	
	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = null;
		if (mirror.isGeoPoint()) 
			algo = new AlgoMirror3D(cons, geo, (GeoPointND) mirror);
		else if(mirror.isGeoLine())
			algo = new AlgoMirror3D(cons, geo, (GeoLineND) mirror);
		else if(mirror instanceof GeoCoordSys2D)
			algo = new AlgoMirror3D(cons, geo, (GeoCoordSys2D) mirror);
		return algo;
	}

}
