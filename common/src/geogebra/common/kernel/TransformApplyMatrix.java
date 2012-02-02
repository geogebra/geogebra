package geogebra.common.kernel;

import geogebra.common.kernel.algos.AlgoApplyMatrix;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoPoint2;

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
	 * @param matrix
	 */
	public TransformApplyMatrix(Construction cons,GeoList matrix) {
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
		AlgoTransformation at = getTransformAlgo(new GeoPoint2(cons));
		cons.removeFromConstructionList(at);
		return at.swapOrientation(true);
	}

}
