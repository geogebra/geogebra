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
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * TEstimate (t confidence interval estimate of a mean)
 */
public class CmdTMeanEstimate extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTMeanEstimate(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {

		case 2:
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoNumeric())) {

				AlgoTMeanEstimate algo = new AlgoTMeanEstimate(cons,
						c.getLabel(), (GeoList) arg[0], (GeoNumeric) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}

		case 4:
			if ((ok[0] = arg[0].isGeoNumeric())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())) {

				AlgoTMeanEstimate algo = new AlgoTMeanEstimate(cons,
						(GeoNumeric) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2], (GeoNumeric) arg[3]);
				algo.getResult().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else if (!ok[2]) {
				throw argErr(c, arg[2]);
			} else if (!ok[3]) {
				throw argErr(c, arg[3]);
			} else {
				throw argErr(c, arg[4]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
