package org.geogebra.common.geogebra3D.kernel3D.transform;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoDilate3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.TransformDilate;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * 3D dilate
 * 
 * @author mathieu
 *
 */
public class TransformDilate3D extends TransformDilate {

	/**
	 * constructor
	 * 
	 * @param cons
	 *            construction
	 * @param ratio
	 *            ratio for dilate
	 * @param center
	 *            center for dilate
	 * 
	 */
	public TransformDilate3D(Construction cons, NumberValue ratio,
			GeoPointND center) {
		super(cons, ratio, center);

	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = new AlgoDilate3D(cons, geo, ratio, center);
		return algo;
	}

}
