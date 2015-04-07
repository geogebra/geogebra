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
 * Mean of a list
 * 
 * @author Michael Borcherds
 * @version 2008-02-18
 */

public class AlgoMean extends AlgoStats1D {

	public AlgoMean(Construction cons, String label, GeoList geoList) {
		super(cons, label, geoList, AlgoStats1D.STATS_MEAN);
	}

	public AlgoMean(Construction cons, GeoList geoList) {
		super(cons, geoList, AlgoStats1D.STATS_MEAN);
	}

	public AlgoMean(Construction cons, String label, GeoList geoList,
			GeoList geoList2) {
		super(cons, label, geoList, geoList2, AlgoStats1D.STATS_MEAN);
	}

	public AlgoMean(Construction cons, GeoList geoList, GeoList geoList2) {
		super(cons, geoList, geoList2, AlgoStats1D.STATS_MEAN);
	}

	@Override
	public Commands getClassName() {
		return Commands.Mean;
	}
}
