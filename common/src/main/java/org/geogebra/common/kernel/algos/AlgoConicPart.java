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
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoConicPart;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;

/**
 * Super class for all algorithms creating conic arcs or sectors.
 */
public abstract class AlgoConicPart extends AlgoElement {

	public GeoConicND conic; // input
	public NumberValue startParam; // input((Construction)
	public NumberValue endParam;
	public GeoConicND conicPart; // output//package private

	public int type;// package private

	/**
	 * Creates a new arc or sector algorithm. The type is either
	 * GeoConicPart.CIRCLE_ARC or GeoConicPart.CIRCLE_ARC.CIRCLE_SECTOR
	 */
	public AlgoConicPart(Construction cons, int type) {// package private
		super(cons);
		this.type = type;
	}

	@Override
	public Commands getClassName() {
		switch (type) {
		case GeoConicPart.CONIC_PART_ARC:
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
