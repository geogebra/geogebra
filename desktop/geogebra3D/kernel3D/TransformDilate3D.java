package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.TransformDilate;
import geogebra.common.kernel.algos.AlgoTransformation;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.kernelND.GeoPointND;


/**
 * 3D dilate
 * @author mathieu
 *
 */
public class TransformDilate3D extends TransformDilate{

	
	/**
	 * constructor
	 * @param cons construction
	 * @param ratio ratio for dilate
	 * @param center center for dilate
	 * 
	 */
	public TransformDilate3D(Construction cons,NumberValue ratio, GeoPointND center) {
		super(cons, ratio, center);

	}
	
	
	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTransformation algo = new AlgoDilate3D(cons, geo, ratio, center);
		return algo;
	}

}
