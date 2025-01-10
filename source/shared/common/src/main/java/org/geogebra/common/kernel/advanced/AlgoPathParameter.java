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

	/**
	 * @param cons
	 *            construction
	 * @param point
	 *            point on path (for other points result is undefined)
	 */
	public AlgoPathParameter(Construction cons, GeoPointND point) {
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

		setOnlyOutput(value);
		setDependencies();
	}

	@Override
	public final void compute() {
		if (!point.isDefined() || !point.isPointOnPath()) {
			value.setUndefined();
			return;
		}

		Path p = point.getPath();

		value.setValue(PathNormalizer.toNormalizedPathParameter(
				point.getPathParameter().getT(), p.getMinParameter(),
				p.getMaxParameter()));
	}

	/**
	 * @return path parameter
	 */
	public GeoNumeric getResult() {
		return value;
	}

}
