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

package org.geogebra.common.kernel.barycentric;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * 
 * Trilinear[point,point,point,coord,coord,coord]
 *
 */
public class CmdTrilinear extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTrilinear(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 6:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoPoint()) && (ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2].isGeoPoint())
					&& (ok[3] = arg[3] instanceof GeoNumberValue)
					&& (ok[4] = arg[5] instanceof GeoNumberValue)
					&& (ok[5] = arg[5] instanceof GeoNumberValue)) {

				AlgoTrilinear algo = new AlgoTrilinear(cons, c.getLabel(),
						(GeoPointND) arg[0], (GeoPointND) arg[1],
						(GeoPointND) arg[2], (GeoNumberValue) arg[3],
						(GeoNumberValue) arg[4], (GeoNumberValue) arg[5]);

				GeoElement[] ret = { algo.getResult().toGeoElement() };
				return ret;

			}
			throw argErr(c, getBadArg(ok, arg));
		default:
			throw argNumErr(c);
		}
	}
}
