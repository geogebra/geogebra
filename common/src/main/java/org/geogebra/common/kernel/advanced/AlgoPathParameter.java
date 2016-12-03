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
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.PathNormalizer;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Adapted from AlgoPerimeterPoly
 */
public class AlgoPathParameter extends AlgoElement {

	// Take a polygon as input
	private GeoPointND point;

	// Output is a GeoNumeric (= a number)
	private GeoNumeric value;

	public AlgoPathParameter(Construction cons, String label, GeoPointND point) {
		this(cons, point);
		value.setLabel(label);
	}

	AlgoPathParameter(Construction cons, GeoPointND point) {
		super(cons);
		this.point = point;

		value = new GeoNumeric(cons);
		setInputOutput();
		compute();
	}

	@Override
	public Commands getClassName() {
		return Commands.PathParameter;
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[1];
		input[0] = (GeoElement) point;

		super.setOutputLength(1);
		super.setOutput(0, value);
		setDependencies();
	}

	@Override
	public final void compute() {
		if (!point.isDefined() || !point.isPointOnPath()) {
			value.setUndefined();
			return;
		}

		Path p = point.getPath();

		// Application.debug(point.getPathParameter().getT()+" "+p.getMinParameter()+" "+p.getMaxParameter());

		value.setValue(PathNormalizer.toNormalizedPathParameter(point
				.getPathParameter().getT(), p.getMinParameter(), p
				.getMaxParameter()));

	}

	public GeoNumeric getResult() {
		return value;
	}

	
}
