package geogebra.common.kernel.cas;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.CasEvaluableFunction;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * Expand[ &lt;Function> ]
 * 
 *
 */
public class CmdExpand extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdExpand(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:
			if (arg[0] instanceof CasEvaluableFunction) {
				
				AlgoExpand algo = new AlgoExpand(cons, c.getLabel(),
						(CasEvaluableFunction) arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			throw argErr(app, c.getName(), arg[0]);

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
