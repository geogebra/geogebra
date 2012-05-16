package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.main.MyError;

/**
 *SurdText
 */
public class CmdScientificText extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdScientificText(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
		case 2:

			if (arg[0].isGeoNumeric() && arg[1].isGeoNumeric()) {
				GeoElement[] ret = { kernelA.ScientificText(c.getLabel(),
						(GeoNumeric) arg[0], (GeoNumeric) arg[1]) };
				return ret;
			} else

				throw argErr(app, c.getName(), arg[arg[0].isGeoNumeric() ? 1 : 0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
