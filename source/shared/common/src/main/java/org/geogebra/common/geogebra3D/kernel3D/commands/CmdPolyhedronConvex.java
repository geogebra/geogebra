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
import org.geogebra.common.main.MyError;

/**
 * Convex polyhedron through points
 *
 */
public class CmdPolyhedronConvex extends CommandProcessor {
	/**
	 * @param kernel
	 *            Kernel
	 */
	public CmdPolyhedronConvex(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {

		int n = c.getArgumentNumber();

		if (n < 4) {
			throw argNumErr(c);
		}

		GeoElement[] arg;

		arg = resArgs(c, info);

		for (int i = 0; i < n; i++) {
			if (!arg[i].isGeoPoint()) {
				throw argErr(c, arg[i]);
			}
		}

		return kernel.getManager3D().polyhedronConvex(c.getLabels(), arg);

	}

}
