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

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.advanced.AlgoReplaceAll;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

public class CmdReplaceAll extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdReplaceAll(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info)
			throws MyError, CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c, info);
		boolean[] ok = new boolean[n];

		if (n == 3) {
			if ((ok[0] = arg[0].isGeoText())
					&& (ok[1] = arg[1].isGeoText())
					&& (ok[2] = arg[2].isGeoText())) {
				AlgoReplaceAll algo = new AlgoReplaceAll(cons, (GeoText) arg[0],
						(GeoText) arg[1], (GeoText) arg[2]);
				algo.getOutput(0).setLabel(c.getLabel());
				return new GeoElement[]{algo.getOutput(0)};
			}
			throw argErr(c, getBadArg(ok, arg));
		}
		throw argNumErr(c);
	}
}
