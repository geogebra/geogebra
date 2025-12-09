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
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings;

/**
 * Chi Squared Distribution
 */
public class CmdRealDistribution1Param extends CommandProcessor {

	private final ProbabilityCalculatorSettings.Dist command;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRealDistribution1Param(Kernel kernel, ProbabilityCalculatorSettings.Dist command) {
		super(kernel);
		this.command = command;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();

		GeoElement[] arg;

		arg = resArgs(c, info);

		GeoBoolean cumulative = null; // default for n=2
		switch (n) {
		case 3:
			if (arg[2].isGeoBoolean()) {
				cumulative = (GeoBoolean) arg[2];
			} else {
				throw argErr(c, arg[2]);
			}

			// fall through
		case 2:
			if (arg[0] instanceof GeoNumberValue) {
				if (arg[1].isGeoFunction() && arg[1]
						.toString(StringTemplate.defaultTemplate).equals("x")) {

					AlgoDistributionDF algo = CmdRealDistribution2Params.getAlgoDF(command,
							(GeoNumberValue) arg[0], null,
							forceBoolean(cumulative, true));
					algo.getResult().setLabel(c.getLabel());
					return algo.getResult().asArray();

				} else if (arg[1] instanceof GeoNumberValue) {

					AlgoRealDistribution1Param algo = new AlgoRealDistribution1Param(cons,
							(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
							cumulative, command);
					GeoElement[] ret = { algo.getResult() };
					ret[0].setLabel(c.getLabel());
					return ret;
				} else {
					throw argErr(c, arg[1]);
				}

			}
			throw argErr(c, arg[0]);

		default:
			throw argNumErr(c);
		}
	}
}
