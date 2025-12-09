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
import org.geogebra.common.main.MyError;
import org.geogebra.common.plugin.GeoClass;

/**
 * ANOVA test
 */
public class CmdANOVA extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdANOVA(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 0:
			throw argNumErr(c);
		case 1: // list of lists, result of XML conversion
			ok[0] = arg[0].isGeoList();
			if (ok[0]) {
				GeoList list = (GeoList) arg[0];

				if (list.size() == 0) {
					throw argErr(c, arg[0]);
				}

				if (list.get(0).isGeoList()) {
					GeoElement[] ret = {
							anovaTest(c.getLabel(), (GeoList) arg[0]) };
					return ret;

				}
				throw argErr(c, arg[0]);
			}

		default:
			GeoList list = wrapInList(arg, arg.length, GeoClass.LIST,
					c);
			if (list != null) {
				GeoElement[] ret = { anovaTest(c.getLabel(), list) };
				return ret;
			}

			// throw error for any other reason ...
			throw argErr(c, arg[0]);
		}
	}

	final private GeoList anovaTest(String label, GeoList dataArrayList) {
		AlgoANOVA algo = new AlgoANOVA(cons, label, dataArrayList);
		GeoList result = algo.getResult();
		return result;
	}

}
