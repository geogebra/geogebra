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
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * ChiSquaredTest of independence and goodness of fit
 */
public class CmdChiSquaredTest extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdChiSquaredTest(Kernel kernel) {
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
			if (arg[0].isGeoList() && arg[0].isMatrix()) {
				AlgoChiSquaredTest algo = new AlgoChiSquaredTest(cons,
						(GeoList) arg[0], null, null);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[]{ algo.getResult() };
			}
			throw argErr(c, arg[0]);
		case 2:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())) {

				AlgoChiSquaredTest algo = new AlgoChiSquaredTest(cons,
						(GeoList) arg[0], (GeoList) arg[1], null);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[]{ algo.getResult() };

			} else {
				throw argErr(c, getBadArg(ok, arg));
			}
		case 3:
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())
					&& (ok[2] = arg[2].isNumberValue())) {

				AlgoChiSquaredTest algo = new AlgoChiSquaredTest(cons,
						(GeoList) arg[0], (GeoList) arg[1], (GeoNumberValue) arg[2]);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[]{ algo.getResult() };

			} else {
				throw argErr(c, getBadArg(ok, arg));
			}
		default:
			throw argNumErr(c);
		}
	}
}
