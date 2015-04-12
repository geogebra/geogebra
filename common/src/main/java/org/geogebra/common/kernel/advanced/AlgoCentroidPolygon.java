/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;

public class AlgoCentroidPolygon extends AlgoElement {

	private GeoPolygon p; // input
	private GeoPointND centroid; // output

	public AlgoCentroidPolygon(Construction cons, String label, GeoPolygon p) {
		super(cons);
		this.p = p;
		centroid = p.newGeoPoint(cons);
		setInputOutput(); // for AlgoElement

		compute();
		centroid.setLabel(label);
	}

	@Override
	public Commands getClassName() {
		return Commands.Centroid;
	}

	// for AlgoElement
	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = p;

		super.setOutputLength(1);
		super.setOutput(0, (GeoElement) centroid);
		setDependencies(); // done by AlgoElement
	}

	GeoPolygon getPolygon() {
		return p;
	}

	public GeoPointND getPoint() {
		return centroid;
	}

	@Override
	public final void compute() {
		if (!p.isDefined()){
			centroid.setUndefined();
			return;
		}
		p.calcCentroid(centroid);
	}

	@Override
	final public String toString(StringTemplate tpl) {
		// Michael Borcherds 2008-03-30
		// simplified to allow better Chinese translation
		return getLoc().getPlain("CentroidOfA", p.getLabel(tpl));
	}

	// TODO Consider locusequability
}
