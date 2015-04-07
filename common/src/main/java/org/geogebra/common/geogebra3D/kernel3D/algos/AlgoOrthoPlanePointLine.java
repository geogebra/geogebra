/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author matthieu
 * @version
 */
public class AlgoOrthoPlanePointLine extends AlgoOrthoPlanePoint {

	/**
	 * 
	 * @param cons
	 * @param label
	 * @param point
	 * @param line
	 */
	public AlgoOrthoPlanePointLine(Construction cons, String label,
			GeoPointND point, GeoLineND line) {
		super(cons, label, point, (GeoElement) line);
	}

	@Override
	protected Coords getNormal() {
		return ((GeoLineND) getSecondInput()).getDirectionForEquation();
	}

	// TODO Consider locusequability

}
