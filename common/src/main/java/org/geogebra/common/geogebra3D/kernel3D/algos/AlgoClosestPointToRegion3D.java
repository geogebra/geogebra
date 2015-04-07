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
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoClosestPointToRegion3D extends AlgoElement3D {

	private Region r;
	private GeoPointND P;

	private GeoPointND geoPointOnRegion;

	public AlgoClosestPointToRegion3D(Construction c, String label, Region r,
			GeoPointND P) {
		super(c);
		this.r = r;
		this.P = P;
		if (r.isGeoElement3D()) {
			geoPointOnRegion = new GeoPoint3D(c);
		} else {
			geoPointOnRegion = new GeoPoint(c);
		}
		geoPointOnRegion.setRegion(r);
		setInputOutput(); // for AlgoElement

		// compute length
		compute();
		geoPointOnRegion.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.ClosestPointRegion;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) r;
		input[1] = (GeoElement) P;

		super.setOutputLength(1);
		super.setOutput(0, (GeoElement) geoPointOnRegion);
		setDependencies(); // done by AlgoElement
	}

	Region getInputRegion() {
		return r;
	}

	GeoPointND getInputPoint() {
		return P;
	}

	public GeoPointND getOutputPoint() {
		return geoPointOnRegion;
	}

	@Override
	public void compute() {
		if (input[0].isDefined() && P.isDefined()) {
			geoPointOnRegion.setCoords(P.getInhomCoordsInD3(), false);
			r.pointChangedForRegion(geoPointOnRegion);
			geoPointOnRegion.updateCoords();
		} else {
			geoPointOnRegion.setUndefined();
		}
	}

}
