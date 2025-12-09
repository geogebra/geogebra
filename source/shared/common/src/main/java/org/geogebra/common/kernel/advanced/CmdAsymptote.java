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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.cas.AlgoAsymptoteFunction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.cas.UsesCAS;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.implicit.AlgoAsymptoteImplicitPoly;
import org.geogebra.common.kernel.implicit.GeoImplicit;
import org.geogebra.common.main.MyError;

/**
 * Asymptote[ &lt;GeoConic&gt; ]
 */
public class CmdAsymptote extends CommandProcessor implements UsesCAS {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAsymptote(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {

				AlgoAsymptote algo = new AlgoAsymptote(cons, c.getLabels(),
						(GeoConic) arg[0]);
				return algo.getAsymptotes();

			} else if (arg[0].isGeoFunction()) {

				AlgoAsymptoteFunction algo = new AlgoAsymptoteFunction(cons,
						c.getLabel(), (GeoFunction) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (arg[0].isGeoImplicitCurve()) {

				AlgoAsymptoteImplicitPoly algo = new AlgoAsymptoteImplicitPoly(
						cons, c.getLabel(), (GeoImplicit) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
