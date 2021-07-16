package org.geogebra.common.geogebra3D.kernel3D.transform;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoTranslate3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Translation
 * 
 */
public class TransformTranslate3D extends Transform {

	private GeoElement transVec;

	/**
	 * @param cons
	 *            construction
	 * @param transVec
	 *            translation vector
	 */
	public TransformTranslate3D(Construction cons, GeoVectorND transVec) {
		this.transVec = (GeoElement) transVec;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTranslate3D algo = new AlgoTranslate3D(cons, geo, transVec);
		return algo;
	}

}