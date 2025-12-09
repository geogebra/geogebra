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
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Cone command processor
 *
 */
public class CmdCone extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdCone(Kernel kernel) {
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

			if ((ok[0] = arg[0] instanceof GeoConicND)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
				return kernel.getManager3D().coneLimited(c.getLabels(),
						(GeoConicND) arg[0], (GeoNumberValue) arg[1]);
			}

			if (!ok[0]) {
				throw argErr(arg[0], c);
			}

			throw argErr(arg[1], c);

		case 3:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoVector())
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {
				GeoElement[] ret = { kernel.getManager3D().cone(c.getLabel(),
						(GeoPointND) arg[0], (GeoVectorND) arg[1],
						(GeoNumberValue) arg[2]) };
				return ret;
			} else if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoPoint())
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {
				return conePointPointRadius(c, (GeoPointND) arg[0],
						(GeoPointND) arg[1], (GeoNumberValue) arg[2]);
			} else if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1] instanceof GeoLineND)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {
				GeoElement[] ret = { kernel.getManager3D().cone(c.getLabel(),
						(GeoPointND) arg[0], (GeoLineND) arg[1],
						(GeoNumberValue) arg[2]) };
				return ret;
			} else {
				if (!ok[0]) {
					throw argErr(arg[0], c);
				} else if (!ok[1]) {
					throw argErr(arg[1], c);
				} else {
					throw argErr(arg[2], c);
				}
			}

		default:
			throw argNumErr(c);
		}

	}

	// overridden by CmdConeInfinite

	/**
	 * @param c
	 *            command
	 * @param p1
	 *            top
	 * @param p2
	 *            bottom center
	 * @param r
	 *            bottom radius
	 * @return cone
	 */
	protected GeoElement[] conePointPointRadius(Command c, GeoPointND p1,
			GeoPointND p2, GeoNumberValue r) {
		return kernel.getManager3D().coneLimited(c.getLabels(), p1, p2, r);
	}

}
