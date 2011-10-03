package geogebra.kernel.commands;


import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.main.MyError;


/**
 * UnitOrthogonalVector[ <GeoLine> ] UnitOrthogonalVector[ <GeoVector> ]
 */
public class CmdUnitOrthogonalVector extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdUnitOrthogonalVector(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (ok[0] = (arg[0].isGeoLine())) {
				GeoElement[] ret = { kernel.UnitOrthogonalVector(c.getLabel(),
						(GeoLine) arg[0]) };
				return ret;
			} else if (ok[0] = (arg[0].isGeoVector())) {
				GeoElement[] ret = { kernel.UnitOrthogonalVector(c.getLabel(),
						(GeoVector) arg[0]) };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, "UnitOrthogonalVector", arg[0]);
			}

		default:
			throw argNumErr(app, "UnitOrthogonalVector", n);
		}
	}
}


