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
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * LimitAbove
 */
public class CmdLimitAbove extends CommandProcessor implements UsesCAS {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLimitAbove(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 2:
			if ((ok = arg[0].isGeoFunction())
					&& (arg[1] instanceof GeoNumberValue)) {
				AlgoLimitAbove algo = new AlgoLimitAbove(cons, c.getLabel(),
						(GeoFunction) arg[0], (GeoNumberValue) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			throw argErr(c, ok ? arg[1] : arg[0]);

			// more than one argument
		default:
			throw argNumErr(c);
		}
	}
}
