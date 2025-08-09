package org.geogebra.common.kernel.advanced;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoFunctionNVar;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.kernelND.GeoConicND;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.main.MyError;

/**
 * Curvature[&lt;Point&gt;,&lt;Curve&gt;], Curvature[&lt;Point&gt;,&lt;Function&gt;]
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
	public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 2:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isRealValuedFunction())) {

				AlgoCurvature algo = new AlgoCurvature(cons, c.getLabel(),
						(GeoPointND) arg[0], (GeoFunction) arg[1]);
				GeoElement[] ret = { algo.getResult() };

				return ret;
			} else if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1] instanceof GeoCurveCartesianND)) {

				AlgoCurvatureCurve algo = new AlgoCurvatureCurve(cons,
						c.getLabel(), (GeoPointND) arg[0],
						(GeoCurveCartesianND) arg[1]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			} else if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1] instanceof GeoFunctionNVar)) {

				// Gaussian Curvature
				AlgoCurvatureSurface algo = new AlgoCurvatureSurface(cons,
						(GeoPointND) arg[0],
						(GeoFunctionNVar) arg[1]);
				algo.getResult().setLabel(c.getLabel());
				GeoElement[] ret = { algo.getResult() };
				return ret;
			}
			if ((ok[0] = arg[0].isGeoPoint())
					&& (ok[1] = arg[1].isGeoConic())) {
				AlgoCurvatureCurve algo = new AlgoCurvatureCurve(cons,
						c.getLabel(), (GeoPointND) arg[0], (GeoConicND) arg[1]);
				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			if (!ok[0]) {
				throw argErr(c, arg[0]);
			}
			throw argErr(c, arg[1]);

		case 3:
			arg = resArgs(c, info);
			if ((ok[0] = arg[0].isNumberValue())
					&& (ok[1] = arg[1].isNumberValue())
					&& (ok[2] = arg[2] instanceof GeoSurfaceCartesianND)) {

				// Gaussian Curvature
				AlgoCurvatureSurfaceParametric algo = new AlgoCurvatureSurfaceParametric(
						cons, c.getLabel(), (GeoNumberValue) arg[0],
						(GeoNumberValue) arg[1],
						(GeoSurfaceCartesianND) arg[2]);

				GeoElement[] ret = { algo.getResult() };
				return ret;
			}

			if (!ok[0]) {
				throw argErr(c, arg[0]);
			}
			if (!ok[1]) {
				throw argErr(c, arg[1]);
			}
			throw argErr(c, arg[2]);

		default:
			throw argNumErr(c);
		}
	}
}
