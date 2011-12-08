package geogebra.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;

/**
 * ApplyMatrix[<Matrix>,<Object>]
 */
class CmdApplyMatrix extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdApplyMatrix(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		String label = c.getLabel();
		int n = c.getArgumentNumber();

		GeoElement[] arg;
		GeoElement[] ret;

		switch (n) {
		case 2:
			arg = resArgs(c);

			if (arg[0].isGeoList()) {

				if (arg[1].isMatrixTransformable() || arg[1].isGeoFunction()
						|| arg[1].isGeoPolygon() || arg[0].isGeoList()) {
					ret = kernel.ApplyMatrix(label, arg[1], (GeoList) arg[0]);
					return ret;
				} else
					throw argErr(app, c.getName(), arg[1]);
			} else
				throw argErr(app, c.getName(), arg[0]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
