/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.algos.AlgoPointOnPath;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * Algo for 3D point on path.
 */
public class AlgoPoint3DOnPath extends AlgoPointOnPath {

	/**
	 * @param cons
	 *            construction
	 * @param path
	 *            path
	 * @param x
	 *            close point x-coord
	 * @param y
	 *            close point y-coord
	 * @param z
	 *            close point z-coord
	 */
	public AlgoPoint3DOnPath(Construction cons, Path path,
			double x, double y, double z) {
		super(cons, path, x, y, z, true);
	}

	/**
	 * @param cons
	 *            construction
	 * @param path
	 *            path
	 * @param param
	 *            path parameter
	 */
	public AlgoPoint3DOnPath(Construction cons, Path path,
			GeoNumberValue param) {
		super(cons, path, param);
	}

	@Override
	protected void createPoint(Path path, double x, double y, double z) {

		P = new GeoPoint3D(cons, path);
		P.setCoords(x, y, z, 1.0);

	}

}
