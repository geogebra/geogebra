package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.algos.AlgoTranslate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoVec3D;

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
