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
 * Triangular[min,max,mode,value] Triangular[min,max,mode,value,cumulative]
 * Triangular[min,max,mode,x]
 */
public class CmdTriangular extends CommandProcessor {
	/**
	 * Creates new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTriangular(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c2, EvalInfo info) throws MyError {
		int n = c2.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		GeoBoolean cumulative = null; // default for n=3
		arg = resArgs(c2, info);

		switch (n) {
		case 5:
			if (arg[4].isGeoBoolean()) {
				cumulative = (GeoBoolean) arg[4];
			} else {
				throw argErr(c2, arg[4]);
			}

			// fall through
		case 4:
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {
				if (arg[3].isGeoFunction() && arg[3]
						.toString(StringTemplate.defaultTemplate).equals("x")) {

					AlgoTriangularDF algo = new AlgoTriangularDF(cons,
							(GeoNumberValue) arg[0],
							(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
							forceBoolean(cumulative, true));
					algo.getResult().setLabel(c2.getLabel());
					return algo.getResult().asArray();

				} else if (arg[3] instanceof GeoNumberValue) {
					AlgoTriangular algo = new AlgoTriangular(cons,
							(GeoNumberValue) arg[0],
							(GeoNumberValue) arg[1], (GeoNumberValue) arg[2],
							(GeoNumberValue) arg[3], cumulative);
					algo.getResult().setLabel(c2.getLabel());
					return algo.getResult().asArray();

				} else {
					throw argErr(c2, arg[3]);
				}
			}
			throw argErr(c2, getBadArg(ok, arg));

		default:
			throw argNumErr(c2);
		}
	}

}
