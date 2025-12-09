/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
		result.setValue(region.isInRegionInRealCoords(pi));
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
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
