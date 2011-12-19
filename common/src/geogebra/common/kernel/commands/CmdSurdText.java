package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;
import geogebra.common.kernel.AbstractKernel;

/**
 *SurdText
 */
public class CmdSurdText extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSurdText(AbstractKernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 1:

			if (arg[0].isGeoNumeric()) {
				GeoElement[] ret = { kernelA.SurdText(c.getLabel(),
						(GeoNumeric) arg[0]) };
				return ret;
			} else if (arg[0].isGeoPoint()) {
				GeoElement[] ret = { kernelA.SurdText(c.getLabel(),
						(GeoPoint2) arg[0]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
