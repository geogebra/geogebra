package geogebra3D.kernel3D;

import geogebra.kernel.AlgoTransformation;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Transform;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;

/**
 * Container for transforms
 * 
 * @author kondr
 * 
 */
public abstract class Transform3D extends Transform{
	

}

/**
 * Translation
 * 
 * @author kondr
 * 
 */
class TransformTranslate3D extends Transform3D {

	private GeoElement transVec;

	/**
	 * @param cons 
	 * @param transVec
	 */
	public TransformTranslate3D(Construction cons,GeoVectorND transVec) {
		this.transVec = (GeoElement) transVec;
		this.cons = cons;
	}

	@Override
	protected AlgoTransformation getTransformAlgo(GeoElement geo) {
		AlgoTranslate3D algo = new AlgoTranslate3D(cons, geo, transVec);
		return algo;
	}

}


