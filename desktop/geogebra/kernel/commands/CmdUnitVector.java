package geogebra.kernel.commands;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.geos.GeoLine;
import geogebra.kernel.geos.GeoVector;

/**
 * UnitVector[ <GeoLine> ] UnitVector[ <GeoVector> ]
 */
class CmdUnitVector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnitVector(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoLine())) {
				GeoElement[] ret = { kernel.UnitVector(c.getLabel(),
						(GeoLine) arg[0]) };
				return ret;
			} else if (ok[0] = (arg[0].isGeoVector())) {
				GeoElement[] ret = { kernel.UnitVector(c.getLabel(),
						(GeoVector) arg[0]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "UnitVector", arg[0]);
			}

		default:
			throw argNumErr(app, "UnitVector", n);
		}
	}
}
