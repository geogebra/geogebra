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

package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoPerimeterLocus;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLocusNDInterface;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.MyError;

/**
 * Perimeter[ &lt;GeoPolygon&gt; ]
 * 
 * Perimeter[ &lt;Conic&gt; ]
 */
public class CmdPerimeter extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdPerimeter(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			// Perimeter[ <GeoPolygon> ]
			arg = resArgs(c, info);
			if (arg[0].isGeoPolygon()) {

				GeoElement[] ret = { getAlgoDispatcher().perimeter(c.getLabel(),
						(GeoPolygon) arg[0]) };
				return ret;

				// Perimeter[ <Conic> ]
			} else if (arg[0].isGeoConic()) {

				GeoElement[] ret = { getAlgoDispatcher()
						.circumference(c.getLabel(), (GeoConicND) arg[0]) };
				return ret;

			} else if (arg[0].isGeoLocus()) {
				// Perimeter[locus]

				AlgoPerimeterLocus algo = new AlgoPerimeterLocus(cons,
						(GeoLocusNDInterface) arg[0]);
				algo.getResult().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else {
				throw argErr(c, arg[0]);
			}

		default:
			throw argNumErr(c);
		}
	}

}
