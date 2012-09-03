package geogebra.common.kernel.algos;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoBoolean;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * Defined[ Object ]
 * Michael Borcherds
 * 2008-03-06
 */
public class CmdDefined extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdDefined(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
		
		switch (n) {
		case 1:
			
			AlgoDefined algo = new AlgoDefined(cons, c.getLabel(),
					 arg[0]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
		
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
