package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DPart;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;

/**
 * for open cylinders
 * 
 * @author mathieu
 *
 */
public class AlgoQuadricComputerCylinderOpen extends
		AlgoQuadricComputerCylinder {

	@Override
	public GeoQuadric3D newQuadric(Construction c) {
		return new GeoQuadric3DPart(c);
	}

	@Override
	public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, double number) {

		direction.calcNorm();
		double altitude = direction.getNorm();

		quadric.setCylinder(origin, direction.mul(1 / altitude), number);

		((GeoQuadric3DPart) quadric).setLimits(0, altitude);
	}

}
