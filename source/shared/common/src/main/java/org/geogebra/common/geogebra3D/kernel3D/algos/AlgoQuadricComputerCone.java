package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * for cones
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricComputerCone extends AlgoQuadricComputer {

	@Override
	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, Coords eigen, double r, double r2) {
		quadric.setCone(origin, direction.normalize(), eigen, r, r2);

	}

	@Override
	public double getNumber(double v) {
		double c = Math.cos(v);
		double s = Math.sin(v);

		if (c < 0 || s < 0) {
			return Double.NaN;
		} else if (DoubleUtil.isZero(c)) { // TODO if c=0 then draws a plane
			return Double.NaN;
		} else if (DoubleUtil.isZero(s)) { // TODO if s=0 then draws a line
			return Double.NaN;
		}

		return s / c;
	}
}
