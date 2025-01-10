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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a plane through a point and parallel to a line (or segment, ...)
 *
 * @author matthieu
 */
public class AlgoLinePointLine3D extends AlgoLinePoint {

	public AlgoLinePointLine3D(Construction cons,
			GeoPointND point, GeoLineND line) {
		super(cons, point, (GeoElement) line);
	}

	@Override
	public Commands getClassName() {
		return Commands.Line;
	}

	@Override
	protected Coords getDirection() {
		return getInputParallel().getMainDirection();
	}

}
