package geogebra3D.kernel3D;

import geogebra.kernel.AlgoVector;
import geogebra.kernel.Construction;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.kernel.kernelND.GeoVectorND;


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


	protected GeoVectorND createNewVector(){

		return new GeoVector3D(cons);

	}


	protected GeoPointND newStartPoint(){

		return new GeoPoint3D(getP());

	}

	protected void setCoords(){
		getVector().setCoords(getQ().getCoordsInD(3).sub(getP().getCoordsInD(3)).get());
	}



}
