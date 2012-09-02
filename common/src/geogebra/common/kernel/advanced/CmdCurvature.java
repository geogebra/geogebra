package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoCurvature;
import geogebra.common.kernel.algos.AlgoCurvatureCurve;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.GeoPoint;
import geogebra.common.main.MyError;

/**
 * Curvature[<Point>,<Curve>], Curvature[<Point>,<Function>]
 * 
 * @author Victor Franco Espino
 */
public class CmdCurvature extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdCurvature(Kernel kernel) {
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
				
				AlgoCurvature algo = new AlgoCurvature(cons, c.getLabel(),
						(GeoPoint) arg[0], (GeoFunction) arg[1]);
				GeoElement[] ret = { algo.getResult() };
				
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {
				
				AlgoCurvatureCurve algo = new AlgoCurvatureCurve(cons, c.getLabel(),
						(GeoPoint) arg[0], (GeoCurveCartesian) arg[1]);
				
				GeoElement[] ret = { algo.getResult() };
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
