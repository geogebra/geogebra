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

package org.geogebra.common.geogebra3D.kernel3D.commands;

import org.geogebra.common.geogebra3D.kernel3D.algos.AlgoCurveCartesian3D;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoCurveCartesian;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.commands.CmdCurveCartesian;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * Curve[ x(t),y(t),z(t),t,from,to]
 */
public class CmdCurveCartesian3D extends CmdCurveCartesian {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdCurveCartesian3D(Kernel kernel) {
		super(kernel);

	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {

		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];

		if (n == 6) {
			// Curve[ <x-coord expression>, <y-coord expression>, <z-coord
			// expression>, <number-var>, <from>, <to> ]
			// Note: x and y and z coords are numbers dependent on number-var

			// create local variable at position 3 and resolve arguments
			GeoElement[] arg = resArgsLocalNumVar(c, 3, 4, 5);

			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4] instanceof GeoNumberValue)
					&& (ok[5] = arg[5] instanceof GeoNumberValue)) {
				GeoElement[] ret = new GeoElement[1];
				ret[0] = kernel.getManager3D().curveCartesian3D(
						(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
						(GeoNumberValue) arg[2], (GeoNumeric) arg[3],
						(GeoNumberValue) arg[4], (GeoNumberValue) arg[5]);
				ret[0].setLabel(c.getLabel());
				return ret;
			}
			for (int i = 0; i < n; i++) {
				if (!ok[i]) {
					throw argErr(c, arg[i]);
				}
			}

		}

		return super.process(c, info);
	}

	@Override
	protected AlgoCurveCartesian getCurveAlgo(ExpressionNode point,
			GeoNumberValue[] coords, GeoElement[] arg) {
		if (coords.length == 2) {
			return super.getCurveAlgo(point, coords, arg);
		}
		return new AlgoCurveCartesian3D(cons, point, coords,
				(GeoNumeric) arg[1], (GeoNumberValue) arg[2],
				(GeoNumberValue) arg[3]);
	}

}
