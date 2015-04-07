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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoDistancePointPlane3D extends AlgoElement3D {

	private GeoPointND point;
	private GeoPlane3D plane;

	private GeoNumeric dist;

	public AlgoDistancePointPlane3D(Construction c, String label,
			GeoPointND point, GeoPlaneND plane) {
		this(c, point, plane);
		dist.setLabel(label);
	}

	public AlgoDistancePointPlane3D(Construction c, GeoPointND point,
			GeoPlaneND plane) {
		super(c);
		this.point = point;
		this.plane = (GeoPlane3D) plane;
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
		input[0] = (GeoElement) point;
		input[1] = plane;

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
		if (!point.isDefined() || !plane.isDefined()) {
			dist.setUndefined();
			return;
		}

		dist.setValue(plane.distance(point));

	}

}
