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

package org.geogebra.common.kernel.algos;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicNDConstants;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;

/**
 * Super class for all algorithms creating conic arcs or sectors.
 */
public abstract class AlgoConicPart extends AlgoElement {

	public GeoConicND conic; // input
	public GeoNumberValue startParam; // input((Construction)
	public GeoNumberValue endParam;
	protected GeoConicND conicPart; // output

	protected int type;

	/**
	 * Creates a new arc or sector algorithm. The type is either
	 * GeoConicPart.CIRCLE_ARC or GeoConicPart.CIRCLE_ARC.CIRCLE_SECTOR
	 */
	protected AlgoConicPart(Construction cons, int type) {
		super(cons);
		this.type = type;
	}

	@Override
	public Commands getClassName() {
		if (type == GeoConicNDConstants.CONIC_PART_ARC) {
			return Commands.Arc;
		}
		return Commands.Sector;
	}

	public GeoConicND getConicPart() {
		return conicPart;
	}

	@Override
	public void compute() {
		conicPart.set(conic);
		((GeoConicPartND) conicPart).setParameters(startParam.getDouble(),
				endParam.getDouble(), true);
	}

}
