package org.geogebra.common.kernel;

import org.geogebra.common.kernel.algos.AlgoDilate;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Dilation
 * 
 * @author Zbynek
 * 
 */
public class TransformDilate extends Transform {

	/** dilation coefficient */
	protected GeoNumberValue ratio;
	/** dilation center */
	protected GeoPointND center;

	/**
	 * @param cons
	 *            construction
	 * @param ratio
	 *            dilation ratio
	 */
	public TransformDilate(Construction cons, GeoNumberValue ratio) {
		this.ratio = ratio;
		this.cons = cons;
	}

	/**
	 * @param cons
	 *            construction
	 * @param ratio
	 *            dilation ratio
	 * @param center
	 *            dilation center
	 */
	public TransformDilate(Construction cons, GeoNumberValue ratio,
			GeoPointND center) {
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
