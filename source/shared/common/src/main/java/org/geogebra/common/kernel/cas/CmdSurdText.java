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
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * SurdText
 */
public class CmdSurdText extends CommandProcessor implements UsesCAS {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSurdText(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 1:

			if (arg[0] instanceof GeoNumberValue) {

				AlgoSurdText algo = new AlgoSurdText(cons, c.getLabel(),
						(GeoNumberValue) arg[0], null);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if (arg[0].isGeoPoint()) {

				AlgoSurdTextPoint algo = new AlgoSurdTextPoint(cons,
						c.getLabel(), (GeoPointND) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			throw argErr(c, arg[0]);

		case 2:

			boolean ok0;
			if ((ok0 = arg[0] instanceof GeoNumberValue)
					&& arg[1].isGeoList()) {

				AlgoSurdText algo = new AlgoSurdText(cons, c.getLabel(),
						(GeoNumberValue) arg[0], (GeoList) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			throw argErr(c, arg[ok0 ? 0 : 1]);

		default:
			throw argNumErr(c);
		}
	}
}
