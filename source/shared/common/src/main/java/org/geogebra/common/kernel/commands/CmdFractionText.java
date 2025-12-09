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
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.algos.AlgoFractionTextPoint;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;

/**
 * FractionText
 */
public class CmdFractionText extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFractionText(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {
		case 1:

			if (arg[0] instanceof GeoNumberValue) {

				AlgoFractionText algo = new AlgoFractionText(cons,
						(GeoNumberValue) arg[0], null);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[]{ algo.getResult() };
			} else if (arg[0].isGeoPoint()) {
				AlgoFractionTextPoint algo = new AlgoFractionTextPoint(cons,
						(GeoPointND) arg[0]);
				algo.getResult().setLabel(c.getLabel());
				return new GeoElement[]{ algo.getResult() };
			}
			throw argErr(c, arg[0]);
		case 2:
			if (!(arg[0] instanceof GeoNumberValue)) {
				throw argErr(c, arg[0]);
			}
			if (!(arg[1] instanceof GeoBoolean)) {
				throw argErr(c, arg[1]);
			}
			AlgoFractionText algo = new AlgoFractionText(cons,
					(GeoNumberValue) arg[0], (GeoBoolean) arg[1]);
			algo.getResult().setLabel(c.getLabel());
			return new GeoElement[]{ algo.getResult() };
		default:
			throw argNumErr(c);
		}
	}
}
