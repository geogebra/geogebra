/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPlane3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.matrix.CoordSys;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author Mathieu
 */
public abstract class AlgoOrthoPlane extends AlgoElement3D {

	private GeoPlane3D plane; // output
	private Coords vn1 = new Coords(3);
	private Coords vn2 = new Coords(3);

	/**
	 * 
	 * @param cons
	 *            construction
	 */
	public AlgoOrthoPlane(Construction cons) {
		super(cons);
		plane = new GeoPlane3D(cons);

	}

	/**
	 * 
	 * @return the plane
	 */
	public GeoPlane3D getPlane() {
		return plane;
	}

	/**
	 * 
	 * @return normal vector to the plane
	 */
	protected abstract Coords getNormal();

	/**
	 * 
	 * @return coords of a point on the plane
	 */
	protected abstract Coords getPoint();

	@Override
	public final void compute() {

		CoordSys coordsys = plane.getCoordSys();

		// recompute the coord sys
		coordsys.resetCoordSys();

		// if cs has "no" direction vector, set undefined and return
		Coords vz = getNormal();
		if (vz.equalsForKernel(0, Kernel.STANDARD_PRECISION)) {
			plane.setUndefined();
			return;
		}

		Coords o = getPoint();
		coordsys.addPoint(o);

		// gets an ortho matrix with coord sys direction vector
		vz.completeOrthonormal3(vn1, vn2);

		coordsys.addVectorWithoutCheckMadeCoordSys(vn1);
		coordsys.addVectorWithoutCheckMadeCoordSys(vn2);

		coordsys.makeOrthoMatrix(false, false);

		// ensure equation vector as integer coeffs if input are integers
		coordsys.setEquationVector(o, vz);

	}

}
