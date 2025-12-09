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
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;

/**
 * Sum of a list of numbers or grouped data
 * 
 * @author Michael Borcherds
 * @version 2008-02-18
 */

public class AlgoSum extends AlgoStats1D {

	public AlgoSum(Construction cons, GeoList geoList) {
		super(cons, geoList, AlgoStats1D.STATS_SIGMAX);
	}

	public AlgoSum(Construction cons, GeoList geoList, GeoList freq) {
		super(cons, geoList, freq, AlgoStats1D.STATS_SIGMAX);
	}

	public AlgoSum(Construction cons, GeoList geoList, GeoNumeric n) {
		super(cons, geoList, null, n, AlgoStats1D.STATS_SIGMAX);
	}

	@Override
	public Commands getClassName() {
		return Commands.Sum;
	}
}
