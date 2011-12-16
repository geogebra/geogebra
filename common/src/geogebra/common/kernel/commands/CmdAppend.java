package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;

/**
 *Append
 */
public class CmdAppend extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdAppend(AbstractKernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (arg[0].isGeoList()) {
				GeoElement[] ret = { kernelA.Append(c.getLabel(),
						(GeoList) arg[0], arg[1]) };
				return ret;
			} else if (arg[1].isGeoList()) {
				GeoElement[] ret = { kernelA.Append(c.getLabel(), arg[0],
						(GeoList) arg[1]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
