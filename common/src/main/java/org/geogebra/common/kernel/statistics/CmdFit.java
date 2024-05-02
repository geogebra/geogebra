/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

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
		GeoElement[] arg = resArgs(c);
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
