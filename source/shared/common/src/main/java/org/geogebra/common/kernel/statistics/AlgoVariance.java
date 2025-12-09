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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoStats1D;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Variance of a list
 * 
 * @author Michael Borcherds
 * @version 2008-02-18
 */

public class AlgoVariance extends AlgoStats1D {

	public AlgoVariance(Construction cons, GeoList geoList) {
		super(cons, geoList, AlgoStats1D.STATS_VARIANCE);
	}

	public AlgoVariance(Construction cons, GeoList geoList,
			GeoList geoList2) {
		super(cons, geoList, geoList2, AlgoStats1D.STATS_VARIANCE);
	}

	@Override
	public Commands getClassName() {
		return Commands.Variance;
	}
}
