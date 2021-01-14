/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoLine3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoRay3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;

/**
 * Compute a line through a point and parallel to a vector
 *
 * @author Mathieu
 */
public class AlgoRayPointVector3D extends AlgoLinePointVector3D {

	/**
	 * @param cons
	 *            construction
	 * @param point
	 *            start point
	 * @param v
	 *            direction vector
	 */
	public AlgoRayPointVector3D(Construction cons,
			GeoPointND point, GeoVectorND v) {
		super(cons,  point, v);
	}

	@Override
	public Commands getClassName() {
		return Commands.Ray;
	}

	@Override
	protected GeoLine3D createLine(Construction cons1) {
		return new GeoRay3D(cons1, getPoint());
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("RayThroughAWithDirectionB",
				getPoint().getLabel(tpl), getInputParallel().getLabel(tpl));
	}

}
