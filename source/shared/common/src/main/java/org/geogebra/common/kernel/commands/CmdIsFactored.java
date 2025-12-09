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
import org.geogebra.common.kernel.algos.AlgoIsFactored;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.main.MyError;

public class CmdIsFactored extends CommandProcessor {

	/**
	 * Create new command processor
	 *
	 * @param kernel
	 *            kernel
	 */
	public CmdIsFactored(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg = resArgs(c, info);

		if (n == 1) {
			if (arg[0] instanceof GeoFunctionable) {
				AlgoIsFactored algo = new AlgoIsFactored(cons, (GeoFunctionable) arg[0]);
				algo.getOutput(0).setLabel(c.getLabel());
				return new GeoElement[]{algo.getOutput(0)};
			}
			throw argErr(c, arg[0]);
		}
		throw argNumErr(c);
	}
}
