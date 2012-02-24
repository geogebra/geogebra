package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 * UnitVector[ <GeoLine> ] UnitVector[ <GeoVector> ]
 */
public class CmdUnitVector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnitVector(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoLine()) {
				GeoElement[] ret = { kernelA.UnitVector(c.getLabel(),
						(GeoLine) arg[0]) };
				return ret;
			} else if (arg[0].isGeoVector()) {
				GeoElement[] ret = { kernelA.UnitVector(c.getLabel(),
						(GeoVector) arg[0]) };
				return ret;
			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
