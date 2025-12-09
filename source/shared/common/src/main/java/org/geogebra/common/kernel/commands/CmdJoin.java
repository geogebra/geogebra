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
import org.geogebra.common.kernel.algos.AlgoJoin;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * Join
 */
public class CmdJoin extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdJoin(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 1:

			ok[0] = arg[0].isGeoList();

			if (ok[0]) {
				GeoElement[] ret = { join(c.getLabel(), (GeoList) arg[0]) };
				return ret;
			} else

			if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else {
				throw argErr(c, arg[1]);
			}

		default:
			// try to create list of numbers
			GeoList list = wrapInList(arg, arg.length, GeoClass.LIST,
					c);
			if (list != null) {
				GeoElement[] ret = { join(c.getLabel(), list) };
				return ret;
			}
			throw argNumErr(c);
		}
	}

	/**
	 * Join[list,list] Michael Borcherds
	 */
	final private GeoList join(String label, GeoList list) {
		AlgoJoin algo = new AlgoJoin(cons, label, list);
		GeoList list2 = algo.getResult();
		return list2;
	}

}
