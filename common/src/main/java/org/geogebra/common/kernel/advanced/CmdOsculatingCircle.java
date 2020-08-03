package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.geos.GeoConic;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.MyError;

/**
 * OsculatingCircle[&lt;Point>,&lt;Function>],OsculatingCircle[&lt;Point>,&lt;Curve>]
 * 
 * @author Victor Franco Espino
 */

public class CmdOsculatingCircle extends CommandProcessor {
	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdOsculatingCircle(Kernel kernel) {
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
					&& (ok[1] = (arg[1].isGeoConic()))) {
				AlgoOsculatingCircleCurve algo = new AlgoOsculatingCircleCurve(
						cons, c.getLabel(), (GeoPoint) arg[0],
						(GeoConic) arg[1]);
				GeoElement[] ret = { algo.getCircle() };
				return ret;
			}
			if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isRealValuedFunction()))) {

				AlgoOsculatingCircle algo = new AlgoOsculatingCircle(cons,
						(GeoPoint) arg[0], (GeoFunction) arg[1]);
				algo.getCircle().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getCircle() };
				return ret;
			} else if ((ok[0] = (arg[0] instanceof GeoPoint))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {

				AlgoOsculatingCircleCurve algo = new AlgoOsculatingCircleCurve(
						cons, c.getLabel(), (GeoPoint) arg[0],
						(GeoCurveCartesian) arg[1]);

				GeoElement[] ret = { algo.getCircle() };
				return ret;
			}
			if (!ok[0]) {
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[1]);

		default:
			throw argNumErr(c);
		}
	}
}
