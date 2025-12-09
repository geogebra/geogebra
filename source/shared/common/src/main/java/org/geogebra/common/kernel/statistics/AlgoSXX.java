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
 * Sxx of a list
 * 
 * @author Michael Borcherds
 * @version 2008-02-18
 */

public class AlgoSXX extends AlgoStats1D {

	/**
	 * @param cons
	 *            construction
	 * @param geoList
	 *            list to process
	 */
	public AlgoSXX(Construction cons, GeoList geoList) {
		super(cons, geoList, AlgoStats1D.STATS_SXX);
	}

	@Override
	public Commands getClassName() {
		return Commands.SXX;
	}
}
