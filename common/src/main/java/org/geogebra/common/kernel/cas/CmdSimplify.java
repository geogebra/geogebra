package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.CasEvaluableFunction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunctionable;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.MyError;

/**
 * Simplify
 */
public class CmdSimplify extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSimplify(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		AlgoElement algo;
		switch (n) {
		case 1:
			if ((arg[0] instanceof CasEvaluableFunction)) {

				algo = new AlgoCasBaseSingleArgument(
						cons, c.getLabel(), (CasEvaluableFunction) arg[0],
						Commands.Simplify);


			} else if (arg[0] instanceof GeoFunctionable) {
				algo = new AlgoCasBaseSingleArgument(
						cons, c.getLabel(),
						((GeoFunctionable) arg[0]).getGeoFunction(),
						Commands.Simplify);


			} else if ((arg[0].isGeoText())) {
				algo = new AlgoSimplifyText(cons,
						c.getLabel(), (GeoText) arg[0]);

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}
			GeoElement[] ret = { algo.getOutput(0) };
			return ret;
			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
