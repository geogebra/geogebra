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

package org.geogebra.common.kernel.barycentric;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.main.MyError;

/**
 * Barycenter[ &lt;List of Points&gt;, &lt; list of weights&gt; ]
 * 
 * @author Darko
 *
 */
public class CmdBarycenter extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdBarycenter(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoList()) && (ok[1] = arg[1].isGeoList())) {

				AlgoBarycenter algo = new AlgoBarycenter(cons, c.getLabel(),
						(GeoList) arg[0], (GeoList) arg[1]);

				GeoElement[] ret = { algo.getResult().toGeoElement() };
				return ret;

			}
			if (!ok[0]) {
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[1]);
		default:
			throw argNumErr(c);
		}
	}
}
