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
import org.geogebra.common.kernel.algos.AlgoStepGraph;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Stick Graph
 * 
 * @author G. Sturr
 * 
 */
public class CmdStepGraph extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdStepGraph(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:

			// StepGraph[ <list of points> ]
			arg = resArgs(c, info);
			if (arg[0].isGeoList()) {
				AlgoStepGraph algo = new AlgoStepGraph(cons, c.getLabel(),
						(GeoList) arg[0]);
				GeoElement[] ret = { algo.getSum() };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		case 2:

			// StepGraph[ <x List>, <y list> ]
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoList())) {

				AlgoStepGraph algo = new AlgoStepGraph(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]);
				GeoElement[] ret = { algo.getSum() };
				return ret;
			}
			// StepGraph[ <list of points>, <join> ]
			else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoBoolean())) {

				AlgoStepGraph algo = new AlgoStepGraph(cons, c.getLabel(),
						(GeoList) arg[0], (GeoBoolean) arg[1]);
				GeoElement[] ret = { algo.getSum() };
				return ret;
			}

			throw argErr(c, getBadArg(ok, arg));

		case 3:

			// StepGraph[ <x List>, <y list>, <join> ]
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoBoolean())) {

				AlgoStepGraph algo = new AlgoStepGraph(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1],
						(GeoBoolean) arg[2]);
				GeoElement[] ret = { algo.getSum() };
				return ret;

				// StepGraph[ <list of points>, <join>, <point style> ]
			} else if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoBoolean())
					&& (ok[2] = arg[2].isGeoNumeric())) {
				AlgoStepGraph algo = new AlgoStepGraph(cons, c.getLabel(),
						(GeoList) arg[0], (GeoBoolean) arg[1],
						(GeoNumeric) arg[2]);
				GeoElement[] ret = { algo.getSum() };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		case 4:

			// StepGraph[ <x List>, <y list>, <join>, <point style> ]
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isGeoBoolean())
					&& (ok[3] = arg[3].isGeoNumeric())) {

				AlgoStepGraph algo = new AlgoStepGraph(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1], (GeoBoolean) arg[2],
						(GeoNumeric) arg[3]);
				GeoElement[] ret = { algo.getSum() };
				return ret;
			}
			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

}