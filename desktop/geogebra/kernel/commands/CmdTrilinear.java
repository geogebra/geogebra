package geogebra.kernel.commands;

import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.geos.GeoElement;
import geogebra.kernel.geos.GeoPoint;
import geogebra.main.MyError;

class CmdTrilinear extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdTrilinear(Kernel kernel) {
		super(kernel);
	}

	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 6:
			arg = resArgs(c);
			if ((ok[0] = arg[0].isGeoPoint()) &&
					(ok[1] = arg[1].isGeoPoint()) &&
					(ok[2] = arg[2].isGeoPoint()) &&
					(ok[3] = arg[3].isNumberValue()) &&
					(ok[4] = arg[5].isNumberValue()) &&
					(ok[5] = arg[5].isNumberValue())) {
				GeoElement[] ret = { kernel.Trilinear(c.getLabel(),
						(GeoPoint)arg[0], (GeoPoint)arg[1], (GeoPoint)arg[2],
						(NumberValue) arg[3], (NumberValue) arg[4], (NumberValue) arg[5])} ;
				return ret;
				
			} else{
				if(!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				if(!ok[1])
					throw argErr(app, c.getName(), arg[1]);
				if(!ok[2])
					throw argErr(app, c.getName(), arg[2]);
				if(!ok[3])
					throw argErr(app, c.getName(), arg[3]);
				if(!ok[4])
					throw argErr(app, c.getName(), arg[4]);
				throw argErr(app, c.getName(), arg[5]);
			}
		default:
			throw argNumErr(app, "Trilinear", n);
		}
	}
}
