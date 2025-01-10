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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;

/**
 * Compute a line through a point and parallel to a vector
 *
 * @author matthieu
 */
public class AlgoLinePointVector3D extends AlgoLinePoint {

	public AlgoLinePointVector3D(Construction cons,
			GeoPointND point, GeoVectorND v) {
		super(cons, point, (GeoElement) v);
	}

	@Override
	public Commands getClassName() {
		return Commands.Line;
	}

	@Override
	protected Coords getDirection() {
		return ((GeoVectorND) getInputParallel()).getCoordsInD3();
	}

	@Override
	public String toString(StringTemplate tpl) {
		return getLoc().getPlain("LineThroughAwithDirectionB",
				getPoint().getLabel(tpl), getInputParallel().getLabel(tpl));
	}
}
