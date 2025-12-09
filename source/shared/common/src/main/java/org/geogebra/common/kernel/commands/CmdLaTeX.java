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
import org.geogebra.common.kernel.algos.AlgoLaTeX;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * FormulaText[ &lt;GeoElement&gt; ]
 */
public class CmdLaTeX extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLaTeX(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:

			arg = resArgs(c, true, info);
			AlgoLaTeX algo = new AlgoLaTeX(cons, c.getLabel(), arg[0]);

			GeoElement[] ret = { algo.getGeoText() };
			return ret;

		case 2:

			arg = resArgs(c, true, info);
			if (arg[1].isGeoBoolean()) {
				GeoElement[] ret2 = { latex(c.getLabel(), arg[0],
						(GeoBoolean) arg[1], null) };
				return ret2;
			}
			throw argErr(c, arg[1]);

		case 3:

			arg = resArgs(c, true, info);
			if (arg[1].isGeoBoolean() && arg[2].isGeoBoolean()) {
				GeoElement[] ret2 = { latex(c.getLabel(), arg[0],
						(GeoBoolean) arg[1], (GeoBoolean) arg[2]) };
				return ret2;
			}

			else if (!arg[1].isGeoBoolean()) {
				throw argErr(c, arg[1]);
			} else {
				throw argErr(c, arg[2]);
			}

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * LaTeX of geo.
	 */
	final private GeoText latex(String label, GeoElement geo,
			GeoBoolean substituteVars, GeoBoolean showName) {
		AlgoLaTeX algo = new AlgoLaTeX(cons, label, geo, substituteVars,
				showName);
		GeoText t = algo.getGeoText();
		return t;
	}

}
