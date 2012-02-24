package geogebra.common.kernel.commands;

import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunctionable;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;

/**
 *SolveODE
 */
public class CmdSolveODE extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdSolveODE(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);

		switch (n) {
//		case 1:
//			if ((arg[0] instanceof GeoFunctionable)) {
//				GeoElement[] ret = { kernel.SolveODE(c.getLabel(),
//						((GeoFunctionable) arg[0]).getGeoFunction()) };
//				return ret;
//			}
		case 5:
			if ((ok[0] = arg[0] instanceof FunctionalNVar)
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())) {
				GeoElement[] ret = { kernelA.SolveODE(c.getLabel(),
						(FunctionalNVar) arg[0], null, (GeoNumeric) arg[1],
						(GeoNumeric) arg[2], (GeoNumeric) arg[3],
						(GeoNumeric) arg[4]) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));
		case 6:
			if ((ok[0] = arg[0] instanceof FunctionalNVar)
					&& (ok[1] = arg[1] instanceof FunctionalNVar)
					&& (ok[2] = arg[2].isGeoNumeric())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())
					&& (ok[5] = arg[5].isGeoNumeric())) {
				GeoElement[] ret = { kernelA.SolveODE(c.getLabel(),
						(FunctionalNVar) arg[0], (FunctionalNVar) arg[1],
						(GeoNumeric) arg[2], (GeoNumeric) arg[3],
						(GeoNumeric) arg[4], (GeoNumeric) arg[5]) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

		case 8: // second order ODE
			if ((ok[0] = arg[0].isGeoFunctionable())
					&& (ok[1] = arg[1].isGeoFunctionable())
					&& (ok[2] = arg[2].isGeoFunctionable())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())
					&& (ok[5] = arg[5].isGeoNumeric())
					&& (ok[6] = arg[6].isGeoNumeric())
					&& (ok[7] = arg[7].isGeoNumeric())) {
				GeoElement[] ret = { kernelA.SolveODE2(c.getLabel(),
						(GeoFunctionable) arg[0], (GeoFunctionable) arg[1],
						(GeoFunctionable) arg[2], (GeoNumeric) arg[3],
						(GeoNumeric) arg[4], (GeoNumeric) arg[5],
						(GeoNumeric) arg[6], (GeoNumeric) arg[7]) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));

			// more than one argument
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
