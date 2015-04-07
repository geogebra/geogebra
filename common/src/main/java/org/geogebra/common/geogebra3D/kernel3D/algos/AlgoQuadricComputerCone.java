package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * for cones
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricComputerCone extends AlgoQuadricComputer {

	@Override
	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, double number) {
		quadric.setCone(origin, direction.normalize(), number);

	}

	@Override
	public double getNumber(double v) {
		double c = Math.cos(v);
		double s = Math.sin(v);

		if (c < 0 || s < 0) {
			return Double.NaN;
		} else if (Kernel.isZero(c)) {// TODO if c=0 then draws a plane
			return Double.NaN;
		} else if (Kernel.isZero(s)) {// TODO if s=0 then draws a line
			return Double.NaN;
		}

		return s / c;
	}
}
