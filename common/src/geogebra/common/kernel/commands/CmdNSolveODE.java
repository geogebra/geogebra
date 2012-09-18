package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.MyError;

/**
 *SolveODE2
 */
public class CmdNSolveODE extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdNSolveODE(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;
		arg = resArgs(c);
		
		if (arg[0].isGeoList() && arg[2].isGeoList() 
				&& ((GeoList)arg[0]).size() != ((GeoList)arg[2]).size()) {
			throw argErr(app, c.getName(), arg[2]);
		}
		
		switch (n) {
		case 4:
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoList())
					&& (ok[3] = arg[3].isGeoNumeric())) {
				GeoElement[] ret = { kernelA.NSolveODE(c.getLabels(),
						(GeoList) arg[0], (GeoNumeric) arg[1],
						(GeoList) arg[2], (GeoNumeric) arg[3],
						new GeoNumeric(cons, 0.000001)) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));
		case 5:
			if ((ok[0] = arg[0].isGeoList())
					&& (ok[1] = arg[1].isGeoNumeric())
					&& (ok[2] = arg[2].isGeoList())
					&& (ok[3] = arg[3].isGeoNumeric())
					&& (ok[4] = arg[4].isGeoNumeric())) {
				GeoElement[] ret = { kernelA.NSolveODE(c.getLabels(),
						(GeoList) arg[0], (GeoNumeric) arg[1],
						(GeoList) arg[2], (GeoNumeric) arg[3],
						(GeoNumeric) arg[4]) };
				return ret;
			}
			throw argErr(app, c.getName(), getBadArg(ok, arg));
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}