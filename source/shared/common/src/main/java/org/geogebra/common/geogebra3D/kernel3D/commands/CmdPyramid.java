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

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * Pyramid[ &lt;GeoPoint3D&gt;, &lt;GeoPoint3D&gt;, &lt;GeoPoint3D&gt;, ... ]
 */
public class CmdPyramid extends CommandProcessor {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdPyramid(Kernel kernel) {
		super(kernel);

	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {

		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c, info);

		if (n == 2) {
			if ((ok[0] = arg[0].isGeoPolygon())
					&& (ok[1] = arg[1].isGeoPoint())) {
				GeoElement[] ret = kernel.getManager3D().pyramid(c.getLabels(),
						(GeoPolygon) arg[0], (GeoPointND) arg[1]);
				return ret;
			} else if ((ok[0] = arg[0].isGeoPolygon())
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
				GeoElement[] ret = kernel.getManager3D().pyramid(c.getLabels(),
						(GeoPolygon) arg[0], (GeoNumberValue) arg[1]);
				return ret;
			} else {
				if (!ok[0]) {
					throw argErr(c, arg[0]);
				}
				throw argErr(c, arg[1]);
			}
		} else if (n > 2) {
			// polygon for given points
			GeoPointND[] points = new GeoPointND[n];
			// check arguments
			for (int i = 0; i < n; i++) {
				if (!arg[i].isGeoPoint()) {
					throw argErr(c, arg[i]);
				}
				points[i] = (GeoPointND) arg[i];
			}
			// everything ok
			GeoElement[] ret = kernel.getManager3D().pyramid(c.getLabels(),
					points);
			return ret;
		} else {
			throw argNumErr(c);
		}

	}

}
