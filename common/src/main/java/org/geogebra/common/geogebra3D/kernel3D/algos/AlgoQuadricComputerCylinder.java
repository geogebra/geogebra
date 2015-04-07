package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * for cylinders
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricComputerCylinder extends AlgoQuadricComputer {

	@Override
	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, double number) {
		quadric.setCylinder(origin, direction.normalize(), number);

	}

	@Override
	public double getNumber(double v) {

		if (Kernel.isZero(v)) {
			return 0;
		} else if (v < 0) {
			return Double.NaN;
		}

		return v;
	}

}
