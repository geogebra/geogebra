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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;

/**
 * Compute a plane through a point and orthogonal to a line (or segment, ...)
 *
 * @author matthieu
 * @version
 */
public class AlgoOrthoPlaneBisectorSegment extends AlgoOrthoPlane {

	private GeoSegmentND segment; // input

	public AlgoOrthoPlaneBisectorSegment(Construction cons, String label,
			GeoSegmentND segment) {
		super(cons);
		this.segment = segment;

		setInputOutput(new GeoElement[] { (GeoElement) segment },
				new GeoElement[] { getPlane() });

		// compute plane
		compute();
		getPlane().setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.PlaneBisector;
	}

	@Override
	protected Coords getNormal() {
		return ((GeoElement) segment).getMainDirection();
	}

	@Override
	protected Coords getPoint() {
		return segment.getPointInD(3, 0.5).getInhomCoordsInSameDimension();
	}

	// TODO Consider locusequability

}
