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

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoQuadric3DLimited;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.CmdIntersectPath;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoQuadricND;
import org.geogebra.common.main.MyError;

/**
 * Processor for IntersectConic and IntersectCircle commands
 *
 */
public class CmdIntersectConic extends CommandProcessor {

	/**
	 * @param kernel
	 *            kernel
	 */
	public CmdIntersectConic(Kernel kernel) {
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

			// between 2 quadrics
			if ((ok[0] = isQuadric(arg[0]))
					&& (ok[1] = isQuadric(arg[1]))) {
				GeoElement[] ret = kernel.getManager3D().intersectAsCircle(
						c.getLabels(), (GeoQuadricND) arg[0],
						(GeoQuadricND) arg[1]);
				return ret;
			}

			// intersection plane/quadric
			GeoElement ret = CmdIntersectPath.processQuadricPlane(kernel, c,
					arg, ok);
			if (ret != null) {
				return new GeoElement[] { ret };
			}

			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}

	protected static boolean isQuadric(GeoElement geo) {
		return geo instanceof GeoQuadric3D
				|| geo instanceof GeoQuadric3DLimited;
	}
}