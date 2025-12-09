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

package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * Simplify
 */
public class CmdSimplify extends CommandProcessor implements UsesCAS {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSimplify(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);
		AlgoElement algo;
		switch (n) {
		case 1:
			if (arg[0] instanceof CasEvaluableFunction) {
				algo = new AlgoCasBaseSingleArgument(cons, c.getLabel(),
						(CasEvaluableFunction) arg[0], Commands.Simplify, info);

			} else if (arg[0] instanceof GeoFunctionable) {
				algo = new AlgoCasBaseSingleArgument(cons, c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						Commands.Simplify, info);

			} else if (arg[0].isGeoText()) {
				algo = new AlgoSimplifyText(cons, c.getLabel(),
						(GeoText) arg[0]);

			} else {
				throw argErr(c, arg[0]);
			}
			GeoElement[] ret = { algo.getOutput(0) };
			return ret;
		// more than one argument
		default:
			throw argNumErr(c);
		}
	}
}
