package geogebra3D.kernel3D;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Matrix.Coords;
import geogebra.common.kernel.algos.AlgoVectorPoint;
import geogebra.common.kernel.kernelND.GeoPointND;
import geogebra.common.kernel.kernelND.GeoVectorND;


/**
 * Vector between two points P and Q.
 * Extends AlgoVector
 * 
 * @author  ggb3D
 */

public class AlgoVectorPoint3D extends AlgoVectorPoint {

	/** constructor
	 * @param cons
	 * @param label
	 * @param P
	 */
	public AlgoVectorPoint3D(Construction cons, String label, GeoPointND P) {
		super(cons, label, P);
	}


	@Override
	protected GeoVectorND createNewVector(){

		return new GeoVector3D(cons);

	}



	@Override
	protected void setCoords(){
		Coords coords = getP().getInhomCoordsInD(3);
		getVector().setCoords(new double[] {coords.getX(),coords.getY(),coords.getZ(),0});
	}



}
