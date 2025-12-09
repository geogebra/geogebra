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
import org.geogebra.common.kernel.algos.AlgoStadium;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;

public class CmdStadium extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdStadium(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		if (n != 3) {
			throw argNumErr(c);
		}
		GeoElement[] args = resArgs(c, info);
		boolean[] ok = new boolean[n];
		if ((ok[0] = args[0].isGeoPoint()) && (ok[1] = args[1].isGeoPoint())
				&& (ok[2] = args[2].isGeoNumeric())) {
			GeoPoint a = (GeoPoint) args[0];
			GeoPoint b = (GeoPoint) args[1];
			GeoNumeric height = (GeoNumeric) args[2];
			AlgoStadium algo = new AlgoStadium(cons, a, b, height);
			algo.getOutput(0).setLabel(c.getLabel());
			return algo.getOutput();
		} else {
			throw argErr(c, getBadArg(ok, args));
		}
	}
}
