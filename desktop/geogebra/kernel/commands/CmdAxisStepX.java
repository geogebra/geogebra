package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.main.MyError;

/**
 *AxisStepX
 */
class CmdAxisStepX extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAxisStepX(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();

		switch (n) {
		case 0:

			GeoElement[] ret = { kernel.AxisStepX(c.getLabel()) };
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
