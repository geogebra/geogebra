package org.geogebra.common.geogebra3D.kernel3D.transform;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoTranslate3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Translation
 * 
 * @author kondr
 * 
 */
public class TransformTranslate3D extends Transform3D {

	private GeoElement transVec;

	/**
	 * @param cons
	 * @param transVec
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