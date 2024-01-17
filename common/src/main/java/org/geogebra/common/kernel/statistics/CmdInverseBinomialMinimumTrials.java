package org.geogebra.common.kernel.statistics;

import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.main.MyError;

public class CmdInverseBinomialMinimumTrials extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdInverseBinomialMinimumTrials(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c, EvalInfo info) throws MyError,
			CircularDefinitionException {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		boolean[] ok = new boolean[n];
		arg = resArgs(c);

		switch (n) {
		case 3:
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {
				AlgoInverseBinomialMinimumTrials algo =
						new AlgoInverseBinomialMinimumTrials(
								c.getLabel(),
								cons ,
								(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
								(GeoNumberValue) arg[2]);
				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else if (!ok[0]) {
				throw argErr(c, arg[0]);
			} else if (!ok[1]) {
				throw argErr(c, arg[1]);
			} else {
				throw argErr(c, arg[2]);
			}

		case 4:
			break;
		default:
			throw argNumErr(c);

		}
		return null;
	}
}
