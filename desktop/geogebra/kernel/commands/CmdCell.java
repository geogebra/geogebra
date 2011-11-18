package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;
import geogebra.main.MyError;

/**
 *Cell
 */
class CmdCell extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCell(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean ok = false;
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if ((ok = (arg[0].isNumberValue()) && arg[1].isNumberValue())) {
				GeoElement[] ret = { kernel.Cell(c.getLabel(),
						(NumberValue) arg[0], (NumberValue) arg[1]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), ok ? arg[1] : arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
