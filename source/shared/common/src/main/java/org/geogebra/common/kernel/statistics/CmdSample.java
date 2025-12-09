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
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * Sample
 * 
 * @author Michael Borcherds
 */
public class CmdSample extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSample(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
				GeoElement[] ret = { sample(c.getLabel(), (GeoList) arg[0],
						(GeoNumberValue) arg[1], null) };
				return ret;

			}
			throw argErr(c, getBadArg(ok, arg));

		case 3:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2].isGeoBoolean())) {
				GeoElement[] ret = { sample(c.getLabel(), (GeoList) arg[0],
						(GeoNumberValue) arg[1], (GeoBoolean) arg[2]) };
				return ret;

			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * Sample[list,n, withReplacement]
	 */
	private GeoElement sample(String label, GeoList list, GeoNumberValue n,
			GeoBoolean withReplacement) {
		AlgoSample algo = new AlgoSample(cons, label, list, n, withReplacement);
		GeoElement ret = algo.getResult();
		return ret;
	}

}
