package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * for cylinders
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricComputerCylinder extends AlgoQuadricComputer {

	@Override
	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, Coords eigen, double r, double r2) {
		quadric.setCylinder(origin, direction.normalize(), eigen, r, r2);

	}

	@Override
	public double getNumber(double v) {

		if (DoubleUtil.isZero(v)) {
			return 0;
		} else if (v < 0) {
			return Double.NaN;
		}

		return v;
	}

}
