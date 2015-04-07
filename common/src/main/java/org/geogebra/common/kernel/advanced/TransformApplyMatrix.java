package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Transform;
import org.geogebra.common.kernel.algos.AlgoTransformation;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;

/**
 * Generic affine transform
 * 
 * @author kondr
 * 
 */
public class TransformApplyMatrix extends Transform {

	private GeoList matrix;

	/**
	 * @param cons
	 *            construction
	 * @param matrix
	 *            transform matrix (2x2 or 3x3 for 2D)
	 */
	public TransformApplyMatrix(Construction cons, GeoList matrix) {
		this.matrix = matrix;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoApplyMatrix algo = new AlgoApplyMatrix(cons, geo, matrix);
		return algo;
	}

	@Override
	public boolean isSimilar() {
		return false;
	}

	@Override
	public boolean changesOrientation() {
		AlgoTransformation at = getTransformAlgo(new GeoPoint(cons));
		cons.removeFromConstructionList(at);
		return at.swapOrientation(null);
	}

}
