package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.algos.AlgoVector;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;


/**
 * Vector between two points P and Q.
 * Extends AlgoVector
 * 
 * @author  ggb3D
 */

public class AlgoVector3D extends AlgoVector {

	/** constructor
	 * @param cons
	 * @param label
	 * @param P
	 * @param Q
	 */
	public AlgoVector3D(Construction cons, String label, GeoPointND P, GeoPointND Q) {
		super(cons, label, P, Q);
	}


	@Override
	protected GeoVectorND createNewVector(){

		return new GeoVector3D(cons);

	}


	@Override
	protected GeoPointND newStartPoint(){

		return new GeoPoint3D(getP());

	}

	@Override
	protected void setCoords(){
		getVector().setCoords(getQ().getInhomCoordsInD(3).sub(getP().getInhomCoordsInD(3)).get());
	}



}
