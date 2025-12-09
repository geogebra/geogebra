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

package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.main.MyError;

/**
 * FirstAxis[ &lt;GeoConic&gt; ]
 */
public class CmdAxis extends CommandProcessor {

	/** 0 for major, 1 for minor */
	protected int axisId;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxis(Kernel kernel, int axisId) {
		super(kernel);
		this.axisId = axisId;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c, info);

			// asymptotes to conic
			if (arg[0].isGeoConic()) {

				AlgoAxis algo = kernel.getAlgoDispatcher().axis(c.getLabel(),
						(GeoConicND) arg[0], axisId);

				GeoElement[] ret = { algo.getAxis().toGeoElement() };
				return ret;
			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
