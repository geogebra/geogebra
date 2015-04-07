package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.algos.AlgoVectorPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Vector between two points P and Q. Extends AlgoVector
 * 
 * @author ggb3D
 */

public class AlgoVectorPoint3D extends AlgoVectorPoint {

	/**
	 * constructor
	 * 
	 * @param cons
	 * @param label
	 * @param P
	 */
	public AlgoVectorPoint3D(Construction cons, String label, GeoPointND P) {
		super(cons, label, P);
	}

	public AlgoVectorPoint3D(Construction cons, GeoPointND P) {
		super(cons, P);
	}

	@Override
	protected GeoVectorND createNewVector() {

		return new GeoVector3D(cons);

	}

	@Override
	protected void setCoords() {
		Coords coords = getP().getInhomCoordsInD3();
		getVector()
				.setCoords(
						new double[] { coords.getX(), coords.getY(),
								coords.getZ(), 0 });
	}

}
