package geogebra.common.kernel.commands;


import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.MyError;
import geogebra.common.kernel.Kernel;


/**
 * Ray[ <GeoPoint>, <GeoPoint> ] 
 * Ray[ <GeoPoint>, <GeoVector> ] 
 */
public class CmdRay extends CommandProcessor {
	/**
	 * Creates new command processor
	 * @param kernel kernel
	 */
	public CmdRay(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2 :
			arg = resArgs(c);

			// line through two points
			if ((ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoPoint()))) {
				GeoElement[] ret =
				{
						kernelA.Ray(
								c.getLabel(),
								(GeoPoint2) arg[0],
								(GeoPoint2) arg[1])};
				return ret;
			}

			// line through point with direction vector
			else if (
					(ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoVector()))) {
				GeoElement[] ret =
				{
						kernelA.Ray(
								c.getLabel(),
								(GeoPoint2) arg[0],
								(GeoVector) arg[1])};
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}

		default :
			throw argNumErr(app, c.getName(), n);
		}
	}
}