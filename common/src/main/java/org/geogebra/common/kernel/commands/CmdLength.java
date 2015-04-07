package org.geogebra.common.kernel.commands;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.AlgoArcLength;
import org.geogebra.common.kernel.algos.AlgoLengthSegment;
import org.geogebra.common.kernel.algos.AlgoLengthVector;
import org.geogebra.common.kernel.algos.AlgoTextLength;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.cas.AlgoLengthCurve;
import org.geogebra.common.kernel.cas.AlgoLengthCurve2Points;
import org.geogebra.common.kernel.cas.AlgoLengthFunction;
import org.geogebra.common.kernel.cas.AlgoLengthFunction2Points;
import org.geogebra.common.kernel.geos.GeoCurveCartesian;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.GeoVec3D;
import org.geogebra.common.kernel.kernelND.GeoConicPartND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSegmentND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;

/**
 * Length[ <GeoVector> ] Length[ <GeoPoint> ] Length[ <GeoList> ] Victor Franco
 * 18-04-2007: add Length[ <Function>, <Number>, <Number> ] add Length[
 * <Function>, <Point>, <Point> ] add Length[ <Curve>, <Number>, <Number> ] add
 * Length[ <Curve>, <Point>, <Point> ]
 */
public class CmdLength extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdLength(Kernel kernel) {
		super(kernel);
	}

	@Override
	public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		boolean[] ok = new boolean[n];
		GeoElement[] arg;

		switch (n) {
		case 1:
			arg = resArgs(c);
			if (arg[0].isGeoVector()) {
				GeoElement[] ret = { length(c.getLabel(), (GeoVectorND) arg[0]) };
				return ret;
			} else if (arg[0].isGeoPoint()) {
				GeoElement[] ret = { length(c.getLabel(), (GeoPointND) arg[0]) };
				return ret;
			} else if (arg[0].isGeoList()) {
				GeoElement[] ret = { getAlgoDispatcher().Length(c.getLabel(),
						(GeoList) arg[0]) };
				return ret;
			} else if (arg[0].isGeoText()) {

				AlgoTextLength algo = new AlgoTextLength(cons, c.getLabel(),
						(GeoText) arg[0]);

				GeoElement[] ret = { algo.getLength() };
				return ret;
			} else if (arg[0].isGeoLocus()) {
				GeoElement[] ret = { getAlgoDispatcher().Length(c.getLabel(),
						(GeoLocus) arg[0]) };
				return ret;
			} else if (arg[0].isGeoSegment()) {

				AlgoLengthSegment algo = new AlgoLengthSegment(cons,
						c.getLabel(), (GeoSegmentND) arg[0]);

				GeoElement[] ret = { algo.getLength() };
				return ret;

			} else if (arg[0].isGeoConicPart()) {
				// Arc length

				AlgoArcLength algo = new AlgoArcLength(cons, c.getLabel(),
						(GeoConicPartND) arg[0]);

				GeoElement[] ret = { algo.getArcLength() };
				return ret;

			} else {
				throw argErr(app, c.getName(), arg[0]);
			}

			// Victor Franco 18-04-2007
		case 3:
			arg = resArgs(c);
			if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoNumeric()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {

				AlgoLengthFunction algo = new AlgoLengthFunction(cons,
						c.getLabel(), (GeoFunction) arg[0],
						(GeoNumeric) arg[1], (GeoNumeric) arg[2]);

				GeoElement[] ret = { algo.getLength() };
				return ret;
			}

			else if ((ok[0] = (arg[0].isGeoFunctionable()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {

				AlgoLengthFunction2Points algo = new AlgoLengthFunction2Points(
						cons, c.getLabel(), (GeoFunction) arg[0],
						(GeoPoint) arg[1], (GeoPoint) arg[2]);

				GeoElement[] ret = { algo.getLength() };
				return ret;
			}

			else if ((ok[0] = (arg[0].isGeoCurveCartesian()))
					&& (ok[1] = (arg[1].isGeoNumeric()))
					&& (ok[2] = (arg[2].isGeoNumeric()))) {

				AlgoLengthCurve algo = new AlgoLengthCurve(cons, c.getLabel(),
						(GeoCurveCartesian) arg[0], (GeoNumeric) arg[1],
						(GeoNumeric) arg[2]);

				GeoElement[] ret = { algo.getLength() };
				return ret;

			}

			else if ((ok[0] = (arg[0].isGeoCurveCartesian()))
					&& (ok[1] = (arg[1].isGeoPoint()))
					&& (ok[2] = (arg[2].isGeoPoint()))) {

				AlgoLengthCurve2Points algo = new AlgoLengthCurve2Points(cons,
						c.getLabel(), (GeoCurveCartesian) arg[0],
						(GeoPoint) arg[1], (GeoPoint) arg[2]);

				GeoElement[] ret = { algo.getLength() };
				return ret;
			}

			else {

				throw argErr(app, c.getName(), getBadArg(ok, arg));
			}

			// Victor Franco 18-04-2007 (end)
		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param v
	 *            vector
	 * @return vector length
	 */
	protected GeoElement length(String label, GeoVectorND v) {
		AlgoLengthVector algo = new AlgoLengthVector(cons, label, (GeoVec3D) v);

		return algo.getLength();
	}

	/**
	 * 
	 * @param label
	 *            label
	 * @param p
	 *            point
	 * @return origin-to-point distance
	 */
	protected GeoElement length(String label, GeoPointND p) {
		AlgoLengthVector algo = new AlgoLengthVector(cons, label, (GeoVec3D) p);

		return algo.getLength();
	}

}
