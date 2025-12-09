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

package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;

/**
 * ZProportion2Estimate[ &lt;Sample Proportion 1 &gt;, &lt;Sample Size 1&gt;,
 * &lt;Sample Proportion 2 &gt;, &lt;Sample Size 2&gt;, &lt;Level&gt; ]
 */
public class CmdZProportion2Estimate extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdZProportion2Estimate(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c, info);

		switch (n) {

		case 5:
			if ((ok[0] = arg[0].isGeoNumeric())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())) {

				AlgoZProportion2Estimate algo = new AlgoZProportion2Estimate(
						cons, (GeoNumeric) arg[0],
						(GeoNumeric) arg[1], (GeoNumeric) arg[2],
						(GeoNumeric) arg[3], (GeoNumeric) arg[4]);
				algo.getResult().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getResult() };
				return ret;

			}

			throw argErr(c, getBadArg(ok, arg));

		default:
			throw argNumErr(c);
		}
	}
}
