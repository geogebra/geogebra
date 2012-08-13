package geogebra.common.kernel.commands;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoCurvatureVector;
import geogebra.common.kernel.algos.AlgoCurvatureVectorCurve;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;

/**
 * CurvatureVector[<Point>,<Curve>], CurvatureVector[<Point>,<Function>]
 * 
 * @author Victor Franco Espino
 */
public class CmdCurvatureVector extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCurvatureVector(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoFunctionable()))) {
				
				AlgoCurvatureVector algo = new AlgoCurvatureVector(cons,c.getLabel(),
						(GeoPoint) arg[0], (GeoFunction) arg[1]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {
				
				AlgoCurvatureVectorCurve algo = new AlgoCurvatureVectorCurve(cons,
						c.getLabel(),
						(GeoPoint) arg[0], (GeoCurveCartesian) arg[1]);
				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else {
				if (!ok[0])
					throw argErr(app, c.getName(), arg[0]);
				throw argErr(app, c.getName(), arg[1]);
			}

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
