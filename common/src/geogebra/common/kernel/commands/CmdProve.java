package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoProve;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;

/**
 * ToolImage
 */
public class CmdProve extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdProve(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {

		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);
	
		switch(n) {
		case 1:
			if (arg[0].isBooleanValue()) {
				
				AlgoProve algo = new AlgoProve(cons, c.getLabel(), arg[0]);

				GeoElement[] ret = { algo.getGeoBoolean() };
				return ret;
				}
			throw argErr(app, c.getName(), arg[0]);
			
		default:
			throw argNumErr(app, c.getName(), n);

		}
	}
}