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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

/**
 * LogNormal distribution
 */
public class CmdLogNormal extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLogNormal(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok;
		GeoElement[] arg;

		GeoBoolean cumulative = null; // default for n=3 (false)
		arg = resArgs(c, info);

		switch (n) {
		case 4:

			if (arg[3].isGeoBoolean()) {
				cumulative = (GeoBoolean) arg[3];
			} else {
				throw argErr(c, arg[3]);
			}

			// fall through
		case 3:
			if ((ok = arg[0] instanceof GeoNumberValue)
					&& (arg[1] instanceof GeoNumberValue)) {
				if (arg[2].isGeoFunction() && arg[2]
						.toString(StringTemplate.defaultTemplate).equals("x")) {

					AlgoLogNormalDF algo = new AlgoLogNormalDF(cons, (GeoNumberValue) arg[0],
							(GeoNumberValue) arg[1],
							forceBoolean(cumulative, true));
					algo.getResult().setLabel(c.getLabel());
					return algo.getResult().asArray();

				} else if (arg[2] instanceof GeoNumberValue) {
					AlgoLogNormal algo = new AlgoLogNormal(cons,
							(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
							(GeoNumberValue) arg[2], cumulative);
					algo.getResult().setLabel(c.getLabel());
					return algo.getResult().asArray();

				} else {
					throw argErr(c, arg[2]);
				}
			}
			throw argErr(c, ok ? arg[1] : arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
