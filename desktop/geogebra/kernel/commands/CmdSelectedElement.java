package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoList;
import geogebra.main.MyError;

/**
 * SelectedElement[ <list>, <n> ] SelectedElement[ <point>, <n> ]
 */
class CmdSelectedElement extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */

	public CmdSelectedElement(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			// list
			if (arg[0].isGeoList())

			{
				GeoElement[] ret = { kernel.SelectedElement(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			}

			// error
			else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
