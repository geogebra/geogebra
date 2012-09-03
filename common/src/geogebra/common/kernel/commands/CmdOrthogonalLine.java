package geogebra.common.kernel.commands;


import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.advanced.AlgoOrthoLinePointConic;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoConic;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.kernel.geos.GeoVector;
import geogebra.common.main.MyError;




/**
 * Orthogonal[ <GeoPoint>, <GeoVector> ] Orthogonal[ <GeoPoint>, <GeoLine> ]
 */
public class CmdOrthogonalLine extends CommandProcessor {

	/**
	* Create new command processor
	* @param kernel kernel
	*/
	public CmdOrthogonalLine(Kernel kernel) {
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

			// line through point orthogonal to vector
			if ((ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoVector()))) {
				GeoElement[] ret =
				{
						getAlgoDispatcher().OrthogonalLine(
								c.getLabel(),
								(GeoPoint) arg[0],
								(GeoVector) arg[1])};
				return ret;
			}

			// line through point orthogonal to another line
			else if (
					(ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoLine()))) {
				GeoElement[] ret =
				{
						getAlgoDispatcher().OrthogonalLine(
								c.getLabel(),
								(GeoPoint) arg[0],
								(GeoLine) arg[1])};
				return ret;
			}
			else if (
					(ok[0] = (arg[0] .isGeoPoint()))
					&& (ok[1] = (arg[1] .isGeoConic()))) {
				
				AlgoOrthoLinePointConic algo = new AlgoOrthoLinePointConic(cons, c.getLabel(),
						(GeoPoint) arg[0],
						(GeoConic) arg[1]);

				return algo.getOutput();
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


