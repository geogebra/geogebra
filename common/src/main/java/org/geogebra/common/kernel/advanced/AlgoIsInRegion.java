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
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;

/**
 * Determine whether point is in region.
 * 
 * @author Zbynek
 * 
 */
public class AlgoIsInRegion extends AlgoElement {

	private GeoPointND pi;
	private Region region;
	private GeoBoolean result;

	/**
	 * Creates new algo
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            output label
	 * @param pi
	 *            point
	 * @param region
	 *            region
	 */
	public AlgoIsInRegion(Construction c, String label, GeoPointND pi,
			Region region) {
		super(c);
		this.pi = pi;
		this.region = region;
		result = new GeoBoolean(cons);
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	public void compute() {
		if (!pi.isDefined() || !region.isDefined()) {
			result.setValue(false);
			return;
		}
		result.setValue(region.isInRegion(pi));
	}

	@Override
	protected void setInputOutput() {
		setOutputLength(1);
		setOutput(0, result);
		input = new GeoElement[2];
		input[0] = (GeoElement) pi;
		input[1] = (GeoElement) region;
		setDependencies();
	}

	/**
	 * Returns true iff point is in region.
	 * 
	 * @return true iff point is in region
	 */
	public GeoBoolean getResult() {
		return result;
	}

	@Override
	public Commands getClassName() {
		return Commands.IsInRegion;
	}

}
