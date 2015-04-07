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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a plane through a point and:
 * <ul>
 * <li>parallel to another plane (or polygon)
 * <li>through a line (or segment, ...) TODO
 * </ul>
 *
 * @author matthieu
 * @version
 */
public abstract class AlgoPlaneThroughPoint extends AlgoElement3D {

	private GeoPointND point; // input
	private GeoElement cs; // input

	private GeoPlane3D plane; // output

	public AlgoPlaneThroughPoint(Construction cons, GeoPointND point) {
		super(cons);
		this.point = point;
		plane = new GeoPlane3D(cons);

	}

	@Override
	public Commands getClassName() {
		return Commands.Plane;
	}

	public GeoPlane3D getPlane() {
		return plane;
	}

	protected GeoPointND getPoint() {
		return point;
	}

	abstract protected GeoElement getSecondInput();

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("PlaneThroughAParallelToB",
				point.getLabel(tpl), getSecondInput().getLabel(tpl));

	}
}
