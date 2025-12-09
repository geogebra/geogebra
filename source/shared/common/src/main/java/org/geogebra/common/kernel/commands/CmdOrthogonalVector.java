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

package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoOrthoVectorLine;
import org.geogebra.common.kernel.algos.AlgoOrthoVectorVector;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.main.MyError;

/**
 * OrthogonalVector[ &lt;GeoLine&gt; ]
 * 
 * OrthogonalVector[ &lt;GeoVector&gt; ]
 */
public class CmdOrthogonalVector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdOrthogonalVector(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isGeoLine()) {

				AlgoOrthoVectorLine algo = new AlgoOrthoVectorLine(cons,
						c.getLabel(), (GeoLine) arg[0]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else if (arg[0] instanceof GeoVec3D) {
				AlgoOrthoVectorVector algo = new AlgoOrthoVectorVector(cons,
						c.getLabel(), (GeoVec3D) arg[0]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else {
				throw argErr(c, arg[0]);
			}

		default:
			throw argNumErr(c);
		}
	}
}
