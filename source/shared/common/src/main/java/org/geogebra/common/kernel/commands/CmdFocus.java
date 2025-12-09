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

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoFocus;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.MyError;

/**
 * Focus[ &lt;GeoConic&gt; ]
 */
public class CmdFocus extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFocus(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);
			if (arg[0].isGeoConic()) {

				AlgoFocus algo = newAlgoFocus(cons, c.getLabels(),
						(GeoConicND) arg[0]);
				return (GeoElement[]) algo.getFocus();

			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * 
	 * @param cons1
	 *            construction
	 * @param labels
	 *            labels for loci
	 * @param c
	 *            conic
	 * @return new AlgoFocus
	 */
	protected AlgoFocus newAlgoFocus(Construction cons1, String[] labels,
			GeoConicND c) {
		return new AlgoFocus(cons1, labels, c);
	}
}
