package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.algos.AlgoTranslate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoVec3D;

/**
 * Translation
 * 
 * @author kondr
 * 
 */
public class TransformTranslate extends Transform {

	private GeoVec3D transVec;

	/**
	 * @param cons
	 *            construction
	 * @param transVec
	 *            translation vector
	 */
	public TransformTranslate(Construction cons, GeoVec3D transVec) {
		this.transVec = transVec;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTranslate algo = new AlgoTranslate(cons, geo, transVec);
		return algo;
	}

}
