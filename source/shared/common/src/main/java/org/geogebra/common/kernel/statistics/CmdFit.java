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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

/**
 * Fit[&lt;List Points&gt;,&lt;List of Functions&gt;] (linear combination)
 * 
 * Fit[&lt;List Points&gt;, &lt;Function&gt;] (nonlinear with sliders as startvalues)
 * 
 * @author Hans-Petter Ulven
 * @version 2011-03-15
 */
public class CmdFit extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFit(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c, info);
		if (n == 2) {
			if (!arg[0].isGeoList()) {
				throw argErr(c, arg[0]);
			}
			if (arg[1].isGeoList()) {

				AlgoFit algo = new AlgoFit(cons, (GeoList) arg[0],
						(GeoList) arg[1]);
				algo.getFit().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getFit().toGeoElement() };
				return ret;
			} else if (arg[1].isGeoFunction()) {

				AlgoFitNL algo = new AlgoFitNL(cons,
						(GeoList) arg[0], (GeoFunction) arg[1]);
				algo.getFitNL().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getFitNL() };
				return ret;
			}
			throw argErr(c, arg[1]);
		}

		throw argNumErr(c);

	}
}
