package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoDilate;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;

/**
 * Dilation
 * 
 * @author kondr
 * 
 */
public class TransformDilate extends Transform {

	private NumberValue ratio;
	private GeoPoint2 center;

	/**
	 * @param cons 
	 * @param ratio
	 */
	public TransformDilate(Construction cons,NumberValue ratio) {
		this.ratio = ratio;
		this.cons = cons;
	}

	/**
	 * @param cons 
	 * @param ratio
	 * @param center
	 */
	public TransformDilate(Construction cons,NumberValue ratio, GeoPoint2 center) {
		this.ratio = ratio;
		this.center = center;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoDilate algo = new AlgoDilate(cons, geo, ratio, center);
		return algo;
	}

}
