package geogebra.kernel.commands;


import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint2;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.MyError;
import geogebra.kernel.Kernel;




/**
 * Orthogonal[ <GeoPoint>, <GeoVector> ] Orthogonal[ <GeoPoint>, <GeoLine> ]
 */
public class CmdOrthogonalLine extends CommandProcessorDesktop {

	/**
	* Create new command processor
	* @param kernel kernel
	*/
	public CmdOrthogonalLine(Kernel kernel) {
		super(kernel);
	}

	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2 :
			arg = resArgs(c);

			// line through point orthogonal to vector
			if ((ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoVector()))) {
				GeoElement[] ret =
				{
						kernel.OrthogonalLine(
								c.getLabel(),
								(GeoPoint2) arg[0],
								(GeoVector) arg[1])};
				return ret;
			}

			// line through point orthogonal to another line
			else if (
					(ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoLine()))) {
				GeoElement[] ret =
				{
						kernel.OrthogonalLine(
								c.getLabel(),
								(GeoPoint2) arg[0],
								(GeoLine) arg[1])};
				return ret;
			}
			else if (
					(ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoConic()))) {
				GeoElement[] ret =
				
						kernel.OrthogonalLineToConic(
								c.getLabel(),
								(GeoPoint2) arg[0],
								(GeoConic) arg[1]);
				return ret;
			}

			// syntax error
			else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				else
					throw argErr(app, c.getName(), arg[1]);
			}

		default :
			throw argNumErr(app, c.getName(), n);
		}
	}
}


