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
		arg = resArgs(c, info);

		switch (n) {
		case 3:
			if ((ok[0] = arg[0] instanceof GeoNumberValue)
					&& (ok[1] = arg[1] instanceof GeoNumberValue)
					&& (ok[2] = arg[2] instanceof GeoNumberValue)) {
				AlgoInverseBinomialMinimumTrials algo =
						new AlgoInverseBinomialMinimumTrials(
								cons ,
								(GeoNumberValue) arg[0], (GeoNumberValue) arg[1],
								(GeoNumberValue) arg[2]);
				algo.getResult().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getResult() };
				return ret;

			} else {
				throw argErr(c, getBadArg(ok, arg));
			}

		case 4:
			break;
		default:
			throw argNumErr(c);

		}
		return null;
	}
}
