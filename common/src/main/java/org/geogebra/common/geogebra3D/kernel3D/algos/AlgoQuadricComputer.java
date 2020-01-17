package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Computer for quadric algos, saying if it's a cone, cylinder, etc.
 * 
 * @author mathieu
 *
 */
public abstract class AlgoQuadricComputer {

	/**
	 * 
	 * @param c
	 *            construction
	 * @return a new quadric
	 */
	public GeoQuadric3D newQuadric(Construction c) {
		return new GeoQuadric3D(c);
	}

	/**
	 * sets the quadric
	 * 
	 * @param quadric
	 *            quadric
	 * @param origin
	 *            origin
	 * @param direction
	 *            direction
	 * @param eigen
	 *            eigenvector
	 * @param r
	 *            radius
	 * @param r2
	 *            second radius
	 */
	abstract public void setQuadric(GeoQuadric3D quadric, Coords origin,
			Coords direction, Coords eigen, double r, double r2);

	/**
	 * return Double.NaN if no usable number
	 * 
	 * @param v
	 *            angle or radius
	 * @return usable number for the quadric
	 */
	abstract public double getNumber(double v);

}
