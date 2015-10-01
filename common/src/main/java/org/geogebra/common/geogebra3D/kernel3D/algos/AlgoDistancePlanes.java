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
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;

public class AlgoDistancePlanes extends AlgoElement3D {

	private GeoPlaneND g3D, h3D;

	private GeoNumeric dist;

	public AlgoDistancePlanes(Construction c, String label, GeoPlaneND g3D,
			GeoPlaneND h3D) {
		this(c, g3D, h3D);
		dist.setLabel(label);
	}

	public AlgoDistancePlanes(Construction c, GeoPlaneND g3D, GeoPlaneND h3D) {
		super(c);
		this.g3D = g3D;
		this.h3D = h3D;
		dist = new GeoNumeric(cons);

		setInputOutput(); // for AlgoElement

		// compute length
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.Distance;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[2];
		input[0] = (GeoElement) g3D;
		input[1] = (GeoElement) h3D;

		super.setOutputLength(1);
		super.setOutput(0, dist);
		setDependencies(); // done by AlgoElement
	}


	public GeoNumeric getDistance() {
		return dist;
	}

	// calc length of vector v
	@Override
	public void compute() {
		if (!g3D.isDefined() || !h3D.isDefined()) {
			dist.setUndefined();
			return;
		}

		dist.setValue(Math.abs(g3D.distanceWithSign(h3D)));

	}

}
