/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoStats1D;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Mean Absolute Deviation of a list
 * 
 * @author Michael Borcherds
 */

public class AlgoMeanAbsoluteDeviation extends AlgoStats1D {

	public AlgoMeanAbsoluteDeviation(Construction cons, GeoList geoList) {
		super(cons, geoList, AlgoStats1D.STATS_MEAN_ABSOLUTE_DEVIATION);
	}

	public AlgoMeanAbsoluteDeviation(Construction cons, GeoList geoList,
			GeoList freq) {
		super(cons, geoList, freq, AlgoStats1D.STATS_MEAN_ABSOLUTE_DEVIATION);
	}

	@Override
	public Commands getClassName() {
		return Commands.mad;
	}
}
