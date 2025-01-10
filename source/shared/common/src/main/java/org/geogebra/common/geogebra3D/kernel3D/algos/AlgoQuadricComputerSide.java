package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * for open cylinders
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricComputerSide extends AlgoQuadricComputer {

	@Override
	public GeoQuadric3D newQuadric(Construction c) {
		return new GeoQuadric3DPart(c);
	}

	@Override
	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, Coords eigen, double r, double r2) {

		quadric.set(origin, direction, eigen, r, r2);

	}

	@Override
	public double getNumber(double v) {
		return 0;
	}

}
