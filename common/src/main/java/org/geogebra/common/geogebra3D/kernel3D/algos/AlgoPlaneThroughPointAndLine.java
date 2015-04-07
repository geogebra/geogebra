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
import org.geogebra.common.kernel.Matrix.CoordSys;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Compute a plane through a point and a line (or segment, ...)
 *
 * @author matthieu
 * @version
 */
public class AlgoPlaneThroughPointAndLine extends AlgoPlaneThroughPoint {

	private GeoLineND line; // input

	public AlgoPlaneThroughPointAndLine(Construction cons, String label,
			GeoPointND point, GeoLineND line) {
		super(cons, point);

		this.line = line;

		setInputOutput(
				new GeoElement[] { (GeoElement) point, (GeoElement) line },
				new GeoElement[] { getPlane() });

		// compute plane
		compute();
		getPlane().setLabel(label);

	}

	@Override
	public final void compute() {

		CoordSys coordsys = getPlane().getCoordSys();

		// recompute the coord sys
		coordsys.resetCoordSys();

		Coords cA = getPoint().getInhomCoordsInD3();
		Coords cB = line.getPointInD(3, 0).getInhomCoordsInSameDimension();
		Coords cC = line.getPointInD(3, 1).getInhomCoordsInSameDimension();

		coordsys.addPoint(cA);
		coordsys.addPoint(cB);
		coordsys.addPoint(cC);

		if (coordsys.makeOrthoMatrix(true, false)) {
			coordsys.setEquationVector(cA, cB, cC);
		}

	}

	@Override
	protected GeoElement getSecondInput() {
		return (GeoElement) line;
	}

	@Override
	final public String toString(StringTemplate tpl) {
		return getLoc().getPlain("PlaneThroughAB", getPoint().getLabel(tpl),
				getSecondInput().getLabel(tpl));

	}

	// TODO Consider locusequability

}
