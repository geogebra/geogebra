/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoElement3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Matrix.CoordMatrixUtil;
import org.geogebra.common.kernel.Matrix.Coords;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoLineND;

public class AlgoClosestPointLines3D extends AlgoElement3D {

	private GeoLineND g3D, h3D;

	private GeoPoint3D geoPointOnG;

	public AlgoClosestPointLines3D(Construction c, String label, GeoLineND g3D,
			GeoLineND h3D) {
		super(c);
		this.g3D = g3D;
		this.h3D = h3D;

		geoPointOnG = new GeoPoint3D(c);
		setInputOutput(); // for AlgoElement

		// compute length
		compute();
		geoPointOnG.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ClosestPoint;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g3D;
		input[1] = (GeoElement) h3D;

		super.setOutputLength(1);
		super.setOutput(0, (GeoElement3D) geoPointOnG);
		setDependencies(); // done by AlgoElement
	}

	GeoLineND getg() {
		return g3D;
	}

	GeoLineND geth() {
		return h3D;
	}

	public GeoPoint3D getPoint() {
		return geoPointOnG;
	}

	@Override
	public void compute() {

		if (!g3D.isDefined() || !h3D.isDefined()) {
			geoPointOnG.setUndefined();
			return;
		}

		Coords[] points = CoordMatrixUtil.nearestPointsFromTwoLines(
				g3D.getStartInhomCoords(), g3D.getDirectionInD3(),
				h3D.getStartInhomCoords(), h3D.getDirectionInD3());

		geoPointOnG.setCoords(points[0], false);

	}

}
