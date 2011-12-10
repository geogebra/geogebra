package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoText;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 *RotateText
 */
class CmdRotateText extends CommandProcessorDesktop {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdRotateText(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:
			if ((ok[0] = (arg[0].isGeoText()))
					&& (ok[1] = arg[1].isGeoNumeric())) {
				GeoElement[] ret = { kernel.RotateText(c.getLabel(),
						(GeoText) arg[0], (GeoNumeric) arg[1]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), ok[0] ? arg[1] : arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
