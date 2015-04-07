package org.geogebra.common.geogebra3D.kernel3D.transform;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoRotate3DLine;
import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoRotate3DPointOrientation;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.TransformRotate;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoDirectionND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 3D rotations
 * 
 * @author mathieu
 *
 */
public class TransformRotate3D extends TransformRotate {

	private GeoDirectionND orientation;

	private GeoLineND line;

	/**
	 * constructor
	 * 
	 * @param cons
	 *            construction
	 * @param angle
	 *            rotation angle
	 * @param center
	 *            center
	 * @param orientation
	 *            orientation
	 */
	public TransformRotate3D(Construction cons, GeoNumberValue angle,
			GeoPointND center, GeoDirectionND orientation) {
		super(cons, angle, center);
		this.orientation = orientation;

	}

	/**
	 * constructor
	 * 
	 * @param cons
	 *            construction
	 * @param angle
	 *            rotation angle
	 * @param line
	 *            line
	 */
	public TransformRotate3D(Construction cons, GeoNumberValue angle,
			GeoLineND line) {
		super(cons, angle);
		this.line = line;

	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = null;
		if (line == null) // rotation about center + orientation
			algo = new AlgoRotate3DPointOrientation(cons, geo, angle, center,
					orientation);
		else
			algo = new AlgoRotate3DLine(cons, geo, angle, line);
		return algo;
	}

}
