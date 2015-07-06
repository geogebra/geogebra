package org.geogebra.common.kernel.advanced;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoConic3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoCurveCartesian3D;
import org.geogebra.common.geogebra3D.kernel3D.geos.GeoPoint3D;
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

				AlgoCurvatureVector algo = new AlgoCurvatureVector(cons,
						c.getLabel(), (GeoPoint) arg[0], (GeoFunction) arg[1]);

				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else if ((ok[0] = (arg[0] instanceof GeoPoint3D))
					&& (ok[1] = (arg[1] instanceof GeoCurveCartesian3D))) {

				AlgoCurvatureVectorCurve3D algo = new AlgoCurvatureVectorCurve3D(
						cons, c.getLabel(), (GeoPoint3D) arg[0],
						(GeoCurveCartesian3D) arg[1]);
				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else if ((ok[0] = (arg[0] instanceof GeoPoint3D))
					&& (ok[1] = (arg[1] instanceof GeoConic3D))) {

				AlgoCurvatureVectorCurve3D algo = new AlgoCurvatureVectorCurve3D(
						cons, c.getLabel(), (GeoPoint3D) arg[0],
						(GeoConic3D) arg[1]);
				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoCurveCartesian()))) {

				AlgoCurvatureVectorCurve algo = new AlgoCurvatureVectorCurve(
						cons, c.getLabel(), (GeoPoint) arg[0],
						(GeoCurveCartesian) arg[1]);
				GeoElement[] ret = { algo.getVector() };
				return ret;
			} else if ((ok[0] = (arg[0].isGeoPoint()))
					&& (ok[1] = (arg[1].isGeoConic()))) {
				AlgoCurvatureVectorCurve algo = new AlgoCurvatureVectorCurve(
						cons, c.getLabel(), (GeoPoint) arg[0],
						(GeoConic) arg[1]);
				GeoElement[] ret = { algo.getVector() };
				return ret;
			}
			if (!ok[0])
				throw argErr(app, c.getName(), arg[0]);
			throw argErr(app, c.getName(), arg[1]);

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}
}
