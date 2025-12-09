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
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Sum of squares of x-coords of a list of Points
 * 
 * @author Michael Borcherds
 * @version 2008-02-23
 */

public class AlgoListSigmaXX extends AlgoStats2D {

	/**
	 * @param cons
	 *            construction
	 * @param geoListx
	 *            list of points
	 */
	public AlgoListSigmaXX(Construction cons, GeoList geoListx) {
		super(cons, geoListx, AlgoStats2D.STATS_SIGMAXX);
	}

	@Override
	public Commands getClassName() {
		return Commands.SigmaXX;
	}
}
