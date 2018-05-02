/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
		switch (type) {
		case GeoConicNDConstants.CONIC_PART_ARC:
			return Commands.Arc;
		default:
			return Commands.Sector;
		}
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
