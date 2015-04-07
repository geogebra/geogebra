package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoVector3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoVector;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Vector between two points P and Q. Extends AlgoVector
 * 
 * @author ggb3D
 */

public class AlgoVector3D extends AlgoVector {

	/**
	 * constructor
	 * 
	 * @param cons
	 * @param label
	 * @param P
	 * @param Q
	 */
	public AlgoVector3D(Construction cons, String label, GeoPointND P,
			GeoPointND Q) {
		super(cons, label, P, Q);
	}

	@Override
	protected GeoVectorND createNewVector() {

		return new GeoVector3D(cons);

	}

	@Override
	protected GeoPointND newStartPoint() {

		return new GeoPoint3D(getP());

	}

	@Override
	protected void setCoords() {
		getVector().setCoords(
				getQ().getInhomCoordsInD3().sub(getP().getInhomCoordsInD3())
						.get());
	}

}
