package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.TransformRotate;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumberValue;
import geogebra.common.kernel.kernelND.GeoDirectionND;
import geogebra.common.kernel.kernelND.GeoLineND;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * 3D rotations
 * @author mathieu
 *
 */
public class TransformRotate3D extends TransformRotate{

	private GeoDirectionND orientation;
	
	private GeoLineND line;
	
	/**
	 * constructor
	 * @param cons construction
	 * @param angle rotation angle
	 * @param center center
	 * @param orientation orientation
	 */
	public TransformRotate3D(Construction cons, GeoNumberValue angle,
			GeoPointND center, GeoDirectionND orientation) {
		super(cons, angle, center);
		this.orientation = orientation;

	}
	
	/**
	 * constructor
	 * @param cons construction
	 * @param angle rotation angle
	 * @param line line
	 */
	public TransformRotate3D(Construction cons, GeoNumberValue angle,
			GeoLineND line) {
		super(cons, angle);
		this.line = line;

	}
	
	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = null;
		if (line==null) //rotation about center + orientation
			algo = new AlgoRotate3DPointOrientation(cons, geo, angle, center, orientation);
		else
			algo = new AlgoRotate3DLine(cons, geo, angle, line);
		return algo;
	}

}
