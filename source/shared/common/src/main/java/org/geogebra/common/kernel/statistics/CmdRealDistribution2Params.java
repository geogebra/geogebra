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

import org.geogebra.common.kernel.Construction;
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
 * Gamma Distribution
 */
public class CmdRealDistribution2Params extends CommandProcessor {

	private final ProbabilityCalculatorSettings.Dist command;

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRealDistribution2Params(Kernel kernel, ProbabilityCalculatorSettings.Dist command) {
		super(kernel);
		this.command = command;
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		arg = resArgs(c, info);

		GeoBoolean cumulative = null; // default for n=3 (false)
		switch (n) {
		case 4:
			if (arg[3].isGeoBoolean()) {
				cumulative = (GeoBoolean) arg[3];
			} else if (arg[3].isNumberValue()
					&& command == ProbabilityCalculatorSettings.Dist.NORMAL) {
				AlgoRealDistribution2ParamsInterval algo = new AlgoRealDistribution2ParamsInterval(
						cons, (GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
						(GeoNumberValue) arg[2], (GeoNumberValue) arg[3], command);
				algo.getResult().setLabel(c.getLabel());
				return algo.getResult().asArray();
			} else {
				throw argErr(c, arg[3]);
			}

			// fall through
		case 3:
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)) {
				if (arg[2].isGeoFunction() && arg[2]
						.toString(StringTemplate.defaultTemplate).equals("x")) {
					AlgoDistributionDF algo = getAlgoDF(command, (GeoNumberValue) arg[0],
							(GeoNumberValue) arg[1], forceBoolean(cumulative, true));
					algo.getResult().setLabel(c.getLabel());
					return algo.getResult().asArray();

				} else if (arg[2] instanceof GeoNumberValue) {

					AlgoRealDistribution2Params algo = new AlgoRealDistribution2Params(cons,
							(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
							(GeoNumberValue) arg[2], cumulative, command);

					GeoElement[] ret = { algo.getResult() };
					ret[0].setLabel(c.getLabel());
					return ret;
				} else {
					throw argErr(c, arg[2]);
				}

			} else {
				throw argErr(c, getBadArg(ok, arg));
			}

		default:
			throw argNumErr(c);
		}
	}

	/**
	 * @param dist distribution
	 * @param param first parameter
	 * @param param2 second parameter
	 * @param cumulative flag for CDF
	 * @return distribution graph algo
	 */
	public static AlgoDistributionDF getAlgoDF(ProbabilityCalculatorSettings.Dist dist,
			GeoNumberValue param, GeoNumberValue param2, GeoBoolean cumulative) {
		Construction cons = param.getConstruction();
		switch (dist) {
		case GAMMA:
			return new AlgoGammaDF(cons, param, param2, cumulative);
		case BETA:
			return new AlgoBetaDF(cons, param, param2, cumulative);
		case CAUCHY:
			return new AlgoCauchyDF(cons, param, param2, cumulative);
		case F:
			return new AlgoFDistributionDF(cons, param, param2, cumulative);
		case WEIBULL:
			return new AlgoWeibullDF(cons, param, param2, cumulative);
		case NORMAL:
			return new AlgoNormalDF(cons, param, param2, cumulative);
		case CHISQUARE:
			return new AlgoChiSquaredDF(cons, param, cumulative);
		case STUDENT:
			return new AlgoTDistributionDF(cons, param, cumulative);
		case EXPONENTIAL:
			return new AlgoExponentialDF(cons, param, cumulative);
		default:
			throw new IllegalStateException("Unexpected distribution " + dist);
		}
	}
}
